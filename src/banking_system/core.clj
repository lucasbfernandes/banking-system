(ns banking-system.core
  (:require 
    [banking-system.http-server.server :as http-server]
  	[banking-system.http-server.routes :refer [app-routes]])
  (:gen-class))

(defn -main
  "Initializes the Banking System application."
  ([port-number]
    (http-server/init app-routes (Integer. port-number)))
  ([]
    (http-server/init app-routes 9000)))
