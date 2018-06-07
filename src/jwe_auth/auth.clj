(ns jwe-auth.auth
  (:require [buddy.core.nonce :as nonce]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.backends.token :refer [jwe-backend]]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [buddy.sign.jwt :as jwt]
            [clj-time.core :as time]
            [jwe-auth.db :refer [auth-data]]
            [jwe-auth.helpers :as h]))

(defonce secret (nonce/random-bytes 32))
(def encrypt-options {:alg :a256kw :enc :a128gcm})
(def auth-backend
  (jwe-backend {:secret secret
                :options encrypt-options
                :token-name "Token"}))
(def jwt-encrypt #(jwt/encrypt % secret encrypt-options))

(defn new-token [claims]
  (-> claims
      (assoc :exp (time/plus (time/now) (time/seconds 3600)))
      jwt-encrypt))

(defn login-token [username password]
  (let [valid? (some-> auth-data
                       (get-in [(keyword username) :password])
                       (= password))]
    (if valid?
      (new-token {:user username}))))

(defn- authorized-for-role?
  [request role]
  (let [claims (:identity request)
        user (:user claims)
        roles (get-in auth-data [(keyword user) :roles])]
    (boolean (get roles role))))

(defn any-access [request]
  (println "any-access uri:" (:uri request))
  true)

(defn admin-access [request]
  (println "admin-access uri:" (:uri request))
  (authorized-for-role? request :admin))

(defn authenticated-access [request]
  (println "authenticated-access uri:" (:uri request))
  (authenticated? request))

(defn mk-restricted-handler
  [handler has-access]
  (wrap-authentication
    (fn [request]
      (if (has-access request)
        (handler request)
        (h/forbidden)))
    auth-backend))

(defn mk-authenticated-access [h]
  (mk-restricted-handler h authenticated-access))

(defn mk-admin-access [h]
  (mk-restricted-handler h admin-access))
