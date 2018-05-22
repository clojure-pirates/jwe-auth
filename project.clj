(defproject jwe-auth "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [mount "0.1.12"]
                 [cprop "0.1.11"]
                 [buddy/buddy-auth "2.1.0"]]
  :main jwe-auth.core
  :resource-paths ["resources" "target/resources"]
  :profiles
  {:dev [:project/dev :profiles/dev]
   :repl {:repl-options {:init-ns user}}
   :profiles/dev {}
   :project/dev {:source-paths ["dev/src"]
                 :resource-paths ["dev/resources"]}})

