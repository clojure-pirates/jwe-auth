(ns jwe-auth.core
  (:require [mount.core :as mount :refer [defstate]]
            [cprop.core :refer [load-config]]
            [ring.adapter.jetty :refer [run-jetty]]))

(defstate config :start (load-config))

(defstate handler :start (fn [req] {:status 201 :body "hello"}))

(defn start-app
  [{:keys [http]}]
  (run-jetty handler {:join? false
                      :port (:port http)}))

(defstate http :start (start-app config)
          :stop (.stop http))

(defn -main
  [& args]
  (mount/start))
