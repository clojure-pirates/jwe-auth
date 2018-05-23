(ns jwe-auth.web
  (:require [mount.core :as mount :refer [defstate]]
            [compojure.route :as route]
            [compojure.core :refer :all]
            [compojure.response :refer [render]]
            [clojure.java.io :as io]
            [ring.util.response :refer [response redirect content-type]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [clj-time.core :as time]
            [buddy.sign.jwt :as jwt]
            [buddy.core.nonce :as nonce]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.backends.token :refer [jwe-backend]]
            [buddy.auth.accessrules :refer [wrap-access-rules]]
            [buddy.auth.middleware :refer [wrap-authentication]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Semantic response helpers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn ok [d] {:status 200 :body d})
(defn bad-request [d] {:status 400 :body d})
(defn unauthenticated [] {:status 401})
(defn unauthorized [] {:status 403})
(defn not-found [] {:status 404})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Access rules
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def secret (nonce/random-bytes 32))
(def opts {:alg :a256kw :enc :a128gcm})

(def auth-data {:admin {:password "secret" :roles #{:admin}}
                :test  {:password "secret" :roles #{:user}}})

(defn- authorized-for-role?
  [request role]
  (let [claims (:identity request)
        user (:user claims)
        roles (get-in auth-data [(keyword user) :roles])]
    (boolean (get roles role))))

(defn any-access [request]
  (println "any-access")
  true)

(defn admin-access [request]
  (println "admin-access")
  (authorized-for-role? request :admin))

(defn operator-access [request]
  (println "operator-access")
  (authorized-for-role? request :operator))

(defn authenticated-access [request]
  (println "authenticated-access")
  (authenticated? request))

(def rules [{:pattern #"^/admin[\/]*.*"
             :handler {:or [admin-access operator-access]}}
            {:pattern #"^/login$"
             :handler any-access}
            {:pattern #"^/.*"
             :handler authenticated-access}])

(defn on-error
  [request value]
  {:status  403
   :headers {}
   :body    "Not authorized"})

(def access-rule-opts {:rules rules :on-error on-error})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Controllers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn home
  [request]
  (if (authenticated? request)
    (ok {:msg (str "Hello " (:identity request))})
    (unauthenticated)))

(defn admin
  [request]
  (if (authenticated? request)
    (ok {:msg (str "Welcome Admin " (:identity request))})
    (unauthenticated)))

(defn- new-token [claims]
  (-> claims
      (assoc :exp (time/plus (time/now) (time/seconds 3600)))
      (jwt/encrypt secret opts)))

(defn login
  [request]
  (let [username (get-in request [:body :username])
        password (get-in request [:body :password])
        valid? (some-> auth-data
                       (get-in [(keyword username) :password])
                       (= password))]
    (if valid?
      (let [token (new-token {:user username})]
        (ok {:token token}))
      (unauthenticated))))

(defn refresh-token
  "Given an authenticated request, will generate a new token with extended
  expiry period which the client may use on subsequent requests.
  see: https://stackoverflow.com/questions/26739167/jwt-json-web-token-automatic-prolongation-of-expiration"
  [request]
  (ok {:token (new-token (:identity request))}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Routes and Middleware
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def app-routes
  (routes
    (GET "/" [] home)
    (GET "/admin" [] admin)
    (POST "/login" [] login)
    (GET "/refresh" [] refresh-token)))

;; Create an instance of auth backend.
(def auth-backend (jwe-backend {:secret  secret
                                :options {:alg :a256kw :enc :a128gcm}}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Entry Point
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defstate handler :start
          (as-> app-routes $
                (wrap-access-rules $ access-rule-opts)
                (wrap-authentication $ auth-backend)
                (wrap-json-response $ {:pretty false})
                (wrap-json-body $ {:keywords? true :bigdecimals? true})))
