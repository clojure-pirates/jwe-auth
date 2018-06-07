(ns jwe-auth.db
  (:require [mount.core :refer [defstate]]))

(defstate auth-data
          :start
          {:admin {:password "secret" :roles #{:admin}}
           :test {:password "secret" :roles #{:user}}})
