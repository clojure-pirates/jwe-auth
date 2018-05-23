(ns jwe-auth.core
  (:require [mount.core :as mount :refer [defstate]]
            [ring.adapter.jetty :refer [run-jetty]]
            [jwe-auth.env :refer [config]]
            [jwe-auth.web :refer [handler]]))

(defn start-app
  [{:keys [http]}]
  (run-jetty handler {:join? false
                      :port (:port http)}))

(defstate http :start (start-app config)
          :stop (.stop http))

(defn -main
  [& args]
  (mount/start))
