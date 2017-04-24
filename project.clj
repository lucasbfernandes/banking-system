(defproject banking-system "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [javax.servlet/servlet-api "2.5"]
                 [ring/ring-core "1.6.0-RC3"]
                 [ring/ring-jetty-adapter "1.5.0"]
                 [ring/ring-defaults "0.2.3"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.5.2"]
                 [clj-time "0.13.0"]]
  :main ^:skip-aot banking-system.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
