(ns dev
  (:require [clojure.tools.namespace.repl :as tn]
            [mount.core :as mount]
            [io.pedestal.http :as http]
            [jwe-auth.pedestal :refer [server]]
            [jwe-auth.routes :as routes]))

(def dev-service-map {:env :dev
                      ::http/routes routes/routes
                      ::http/type :jetty
                      ::http/port 3000
                      ::http/join? false})

(defn start []
  (mount/start-with {#'jwe-auth.pedestal/service-map dev-service-map}))

(defn stop []
  (mount/stop))

(defn refresh []
  (stop)
  (tn/refresh))

(defn refresh-all []
  (stop)
  (tn/refresh-all))

(defn go
  "starts all states defined by defstate"
  []
  (start)
  :ready)

(defn reset
  "stops all states defined by defstate, reloads modified source files, and restarts the states"
  []
  (stop)
  (start)
  (tn/refresh :after 'dev/go))