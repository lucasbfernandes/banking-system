(ns banking-system.http-server.handlers
  (:require
    [banking-system.helpers.fn :as helper]
    [banking-system.account-management.accounts :as accounts]
    [banking-system.account-management.operations :as operations]
    [banking-system.account-management.statements :as statements]))

(defn create-account [request]
  (accounts/insert-account 
    accounts/accounts-map 
    (helper/get-json-param request "name") 
    (helper/get-json-param request "email")))

(defn account-operation [request type]
  (operations/insert-operation
    accounts/accounts-map
    (helper/get-json-param request "account-number")
    (helper/get-json-param request "description")
    (helper/get-json-param request "amount")
    (helper/get-json-param request "date")
    type))

(defn account-credit [request]
  (account-operation request "C"))

(defn account-debit [request]
  (account-operation request "D"))

(defn account-balance [request]
  (statements/retrieve-balance
    accounts/accounts-map
    (helper/get-json-param request "account-number")
    (helper/get-today-date)))

(defn account-statement [request]
  (statements/get-account-statement
    accounts/accounts-map
    (helper/get-json-param request "account-number")
    (-> (helper/get-json-param request "begin-date")
        (helper/format-date))
    (-> (helper/get-json-param request "end-date")
        (helper/format-date))))    