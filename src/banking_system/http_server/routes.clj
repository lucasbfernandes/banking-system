(ns banking-system.http-server.routes
  (:require 
    [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
    [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
    [ring.util.response :refer [response]]
    [compojure.core :refer [defroutes POST]]
    [banking-system.http-server.handlers :as handlers]
    [banking-system.settings.endpoints :as endpoints]))

(defroutes routes

  (POST endpoints/create-account request
    (response (handlers/create-account request)))

  (POST endpoints/account-credit request
    (response (handlers/account-credit request)))

  (POST endpoints/account-debit request
    (response (handlers/account-debit request)))

  (POST endpoints/account-balance request
    (response (handlers/account-balance request)))

  (POST endpoints/account-statement request
    (response (handlers/account-statement request))))

(def app-routes
  (-> routes
  	  (wrap-json-body)
  	  (wrap-json-response)
      (wrap-defaults api-defaults)))
