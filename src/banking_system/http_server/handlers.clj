(ns banking-system.http-server.handlers
  (:require
    [banking-system.helpers.fn :as fn]
    [banking-system.settings.constants :as constants]
    [banking-system.account-management.accounts :as accounts]
    [banking-system.account-management.operations :as operations]
    [banking-system.account-management.statements :as statements]))

(defn create-account [request]
  (accounts/create-account 
    accounts/accounts-map 
    (fn/get-json-param request "name") 
    (fn/get-json-param request "email")))

(defn account-operation [request type]
  (operations/create-operation
    accounts/accounts-map
    (fn/get-json-param request "account-number")
    (fn/get-json-param request "description")
    (fn/get-json-param request "amount")
    (fn/get-json-param request "date")
    type))

(defn account-credit [request]
  (account-operation request constants/credit-string))

(defn account-debit [request]
  (account-operation request constants/debit-string))

(defn account-balance [request]
  (statements/get-account-balance
    accounts/accounts-map
    (fn/get-json-param request "account-number")
    (fn/get-today-date)))

(defn account-statement [request]
  (statements/get-account-statement
    accounts/accounts-map
    (fn/get-json-param request "account-number")
    (fn/get-json-param request "begin-date")
    (fn/get-json-param request "end-date")))    