(ns jwe-auth.pedestal
  (:require [mount.core :refer [defstate]]
            [environ.core :refer [env]]
            [io.pedestal.http :as http]
            [io.pedestal.interceptor :refer [interceptor interceptor-name]]
            [jwe-auth.routes :as routes]))

(defn test-env?
  [service-map]
  (= :test (:env service-map)))

(defn pedestal-config
  []
  {:env :prod
   ::http/routes routes/routes
   ::http/type :jetty
   ::http/port (Integer/parseInt (env :port))
   ::http/join? false})

(defstate service-map
          :start (pedestal-config))

(defstate server
          :start (cond-> service-map
                         true http/create-server
                         (not (test-env? service-map)) http/start)
          :stop (http/stop server))
