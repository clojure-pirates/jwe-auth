(ns jwe-auth.routes
  (:require [jwe-auth.auth :as auth]
            [jwe-auth.helpers :as h]))

(defn home
  [_request]
  (h/ok {:msg "Hello World!"}))

(defn admin
  [request]
  (h/ok {:msg (str "Hello Admin " (:identity request))}))

(defn post-comment
  [request]
  (let [{:keys [author]} (:params request)
        {:keys [comment]} (:params request)]
    (if (= author (get-in request [:identity :user]))
      (h/ok {:msg comment})
      (h/forbidden))))

(defn- respond-with-token [token]
  (h/ok {:token token}))

(defn login
  [request]
  (let [username (get-in request [:params :username])
        password (get-in request [:params :password])
        token (auth/login-token username password)]
    (if token
      (respond-with-token token)
      (h/unauthorized))))

(defn refresh-token
  "Given an authenticated request, will generate a new token with extended
  expiry period which the client may use on subsequent requests.
  see: https://stackoverflow.com/questions/26739167/jwt-json-web-token-automatic-prolongation-of-expiration"
  [request]
  (respond-with-token (auth/new-token (:identity request))))

(def common-interceptors [h/print-headers h/coerce-body h/negotiate-content h/parse-body-params h/merge-body-params])

;; Tabular routes
(def routes #{["/" :get (conj common-interceptors home) :route-name :home]
              ["/admin" :get (conj common-interceptors (auth/mk-admin-access admin)) :route-name :admin]
              ["/author/:author/comment" :post (conj common-interceptors (auth/mk-authenticated-access post-comment)) :route-name :post-comment]
              ["/login" :post (conj common-interceptors login) :route-name :login]
              ["/refresh" :get (conj common-interceptors (auth/mk-authenticated-access refresh-token)) :route-name :refresh-token]})