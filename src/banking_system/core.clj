(ns banking-system.core
  (:require 
  	[banking-system.settings.constants :as constants]
    [banking-system.http-server.server :as http-server]
  	[banking-system.http-server.routes :refer [app-routes]])
  (:gen-class))

(defn -main
  "Initializes the Banking System application. Receives the port-number
  as a optional parameter."
  ([port-number]
    (http-server/init app-routes (Integer. port-number)))
  ([]
    (http-server/init app-routes constants/default-port)))
