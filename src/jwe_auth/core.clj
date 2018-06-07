(ns jwe-auth.core
  (:require [mount.core :as mount]
            [jwe-auth.pedestal :refer [server service-map]])
  (:gen-class))

(defn -main
  [& _args]
  (mount/start))
