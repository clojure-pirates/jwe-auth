(ns dev
  (:require [clojure.tools.namespace.repl :as tn]
            [mount.core :as mount]
            [jwe-auth.env :refer [config]]
            [jwe-auth.pedestal :refer [server service-map]]))

(defn start []
  (mount/start))

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