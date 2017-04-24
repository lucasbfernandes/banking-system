(ns banking-system.http-server.server
  (:require 
    [ring.adapter.jetty :as jetty]))

(defn init
  "Initializes the Banking System HTTP server on the specified port."
  [app-routes port-number]
  (jetty/run-jetty app-routes {:port port-number}))
