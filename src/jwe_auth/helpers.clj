(ns jwe-auth.helpers
  (:require [cheshire.core :as json]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.content-negotiation :as content-neg]))

(def supported-types ["text/html" "application/edn" "application/json" "text/plain"])

(def negotiate-content (content-neg/negotiate-content supported-types))

(def coerce-body
  {:name ::coerce-body
   :leave
   (fn [context]
     (let [accepted (get-in context [:request :accept :field] "text/plain")
           response (get context :response)
           body (get response :body)
           coerced-body (case accepted
                          "text/html" body
                          "text/plain" body
                          "application/edn" (pr-str body)
                          "application/json" (json/generate-string body))
           updated-response (assoc response
                              :headers {"Content-Type" accepted}
                              :body coerced-body)]
       (assoc context :response updated-response)))})

(def print-headers
  {:name ::print-headers
   :enter (fn [ctx]
            (printf "\n\n|<-- %s\n" (get-in ctx [:request :headers]))
            ctx)
   :leave (fn [ctx]
            (printf "\n\n-->| %s\n" (get-in ctx [:request :headers]))
            ctx)})

(def parse-body-params (body-params/body-params))

(def merge-body-params
  {:name ::merge-params
   :enter (fn [ctx]
            (let [req (:request ctx)
                  params (-> (get req :params {})
                             (merge (:json-params req)
                                    (:edn-params req)
                                    (:transit-params req)
                                    (:form-params req)
                                    (:path-params req)
                                    (:query-params req)))]
              (printf "\n\nparams: %s\n" params)
              (update-in ctx
                         [:request :params]
                         (fn [_old] params))))})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Semantic response helpers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- error [status msg]
  {:status status :headers {} :body {:msg msg}})
(defn ok [d] {:status 200 :headers {} :body d})
(defn unauthorized [] (error 401 "Not authorized"))
(defn forbidden [] (error 403 "Not allowed"))