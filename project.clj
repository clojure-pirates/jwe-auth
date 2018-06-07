(defproject jwe-auth "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]

                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/log4j-over-slf4j "1.7.25"]

                 [mount "0.1.12"]
                 [cprop "0.1.11"]
                 [clj-time "0.14.4"]

                 [io.pedestal/pedestal.service "0.5.3"]
                 [io.pedestal/pedestal.jetty "0.5.3"]

                 [buddy/buddy-auth "2.1.0"]
                 [buddy/buddy-hashers "1.3.0"]]
  :main jwe-auth.core
  :resource-paths ["resources" "config"]
  :profiles
  {:dev {:source-paths ["dev/src"]
         :resource-paths ["dev/resources" "dev/config"]
         :dependencies [[org.clojure/tools.namespace "0.2.11"]]}

   :repl {:repl-options {:init-ns user}}

   :uberjar {:omit-source true
             :aot :all
             :uberjar-name "jwe-auth.jar"
             :source-paths ["src"]
             :resource-paths ["resources" "config"]}})

