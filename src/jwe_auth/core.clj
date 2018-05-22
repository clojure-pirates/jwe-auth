(ns jwe-auth.core
  (:require [mount.core :as mount :refer [defstate]]
            [ring.adapter.jetty :refer [run-jetty]]
            [jwe-auth.config :as config]))

(defn handler [req] {:status 201 :body "hello"})

(defn start-app
  [{:keys [http]}]
  (run-jetty handler {:join? false
                      :port (:port http)}))

(defstate app :start (start-app config)
          :stop (.stop app))

(defn -main
  [& args]
  (mount/start))
