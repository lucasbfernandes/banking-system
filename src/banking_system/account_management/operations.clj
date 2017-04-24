(ns banking-system.account-management.operations
  (:require
    [banking-system.helpers.fn :as fn]
    [banking-system.settings.constants :as constants]
    [banking-system.settings.messages :as messages]))

(defn wrap-operation
  "Wraps a new operation with the provided account-number, description,
  amount, date and type. Date must be on the yyyy-mm-dd format."
  [account-number description amount date type]
  {:account-number account-number
   :description description 
   :amount amount
   :date (fn/format-date date)
   :type type})

(defn get-operations
  "Retrieves the operations structure of a given account."
  [accounts-map account-number]
  ((@accounts-map account-number) :operations))

(defn get-operation-amount
  "Takes an operation as parameter and return its amount based on its type.
  (e.g. If an operation has an amount of 2000 and is a debit operation,
  then get-operation-amount will return -2000)."
  [operation]
  (if (= (operation :type) constants/credit-string)
    (+ 0 (operation :amount))
    (- 0 (operation :amount))))

(defn insert-operation
  "Updates atom to hold a new operation."
  [operation accounts-map account-number]
    (swap! 
      (get-operations accounts-map account-number)
      fn/insert-sorted operation
      fn/operations-comparator))

(defn create-operation
  "Inserts a new operation (Credit or Debit) into an account in the accounts
  map. If the account does not exit or one of the values is nil, return failure."
  [accounts-map account-number description amount date type]
  (if (and accounts-map account-number description amount date
           type (@accounts-map account-number))
    (do
      (-> (wrap-operation account-number description amount date type)
          (insert-operation accounts-map account-number))
      (fn/retval-success))
    (fn/retval-failure messages/MSG_0002)))