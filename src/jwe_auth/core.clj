(ns jwe-auth.core
  (:require [mount.core :as mount]
            [jwe-auth.env :refer [config]]
            [jwe-auth.pedestal :refer [server service-map]]))

(defn -main
  [& args]
  (mount/start))
