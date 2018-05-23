(ns jwe-auth.env
  (:require [mount.core :refer [defstate]]
            [cprop.core :refer [load-config]]))

(defstate config :start (load-config))
