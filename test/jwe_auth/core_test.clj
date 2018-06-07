(ns jwe-auth.core-test
  (:require [clojure.test :refer :all]
            [mount.core :as mount]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :refer [response-for]]
            [jwe-auth.core :refer :all]
            [jwe-auth.pedestal :refer [server]]
            [jwe-auth.routes :as routes]))

(def url-for (route/url-for-routes
               (route/expand-routes routes/routes)))

(def test-service-map {:env :test
                       ::http/routes routes/routes
                       ::http/type :jetty
                       ::http/port 3001
                       ::http/join? false})
(deftest home-page-test
  (let [_ (mount/start-with {#'jwe-auth.pedestal/service-map test-service-map})
        {:keys [status body headers]} (response-for (::http/service-fn server)
                                                    :get
                                                    (url-for :home)
                                                    :headers {"Accept" "application/edn"})]
    (is (= 200 status))
    (is (= "application/edn" (get headers "Content-Type")))
    (is (= "{:msg \"Hello World!\"}" body))))

