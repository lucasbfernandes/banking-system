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
    (fn/get-json-param request constants/name-param) 
    (fn/get-json-param request constants/email-param)))

(defn account-operation [request type]
  (operations/create-operation
    accounts/accounts-map
    (fn/get-json-param request constants/account-number-param)
    (fn/get-json-param request constants/description-param)
    (fn/get-json-param request constants/amount-param)
    (fn/get-json-param request constants/date-param)
    type))

(defn account-credit [request]
  (account-operation request constants/credit-string))

(defn account-debit [request]
  (account-operation request constants/debit-string))

(defn account-balance [request]
  (statements/get-account-balance
    accounts/accounts-map
    (fn/get-json-param request constants/account-number-param)
    (fn/date-string (fn/get-today-date))))

(defn account-statement [request]
  (statements/get-account-statement
    accounts/accounts-map
    (fn/get-json-param request constants/account-number-param)
    (fn/get-json-param request constants/begin-date-param)
    (fn/get-json-param request constants/end-date-param)))    