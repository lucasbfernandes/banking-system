(ns banking-system.account-management.accounts
  (:require
    [banking-system.helpers.fn :as fn]
    [banking-system.account-management.operations :as operations]
    [banking-system.settings.constants :as constants]
    [banking-system.settings.messages :as messages]))

(def accounts-map (atom {}))

(defn wrap-account
  "Wraps a new account with the provided username and email values.
  Also creates a :operations atom vector that will hold account
  operations (i.e. Insert and remove money)."
  [username email]
  {:name username
   :email email
   :operations (atom [])})

(defn generate-account-number
  "Generates a random account number that is not assigned to a user yet."
  [accounts-map]
  (let [account-number (str (+ 100000 (rand-int 900000)))]
    (if (contains? @accounts-map account-number)
      (generate-account-number accounts-map)
      account-number)))

(defn generate-dummy-accounts-map
 "Generates an account map that contains one account with the specified account-number.
 Also generates a number of operations in the account. There will be <length> operations of
 type <type> and amount <amount>."
 [account-number length amount type]
 (let [dummy-map (atom {account-number (wrap-account constants/dummy-username constants/dummy-email)})]
   (loop [pos 0]
     (if (= pos length)
       dummy-map
       (do 
         (operations/insert-operation
           (operations/wrap-operation 
             account-number
             constants/dummy-description
             amount
             constants/dummy-date
             type)
           dummy-map
           account-number)
         (recur (inc pos)))))))

; TODO should return ACCOUNTS-MAP and not account-number
; TODO should not accept string as account-number
(defn remove-account
  "Removes an account from the account map and returns the account number."
  [account-number accounts-map]
  (swap! accounts-map
    dissoc account-number)
  account-number)

; TODO should return ACCOUNTS-MAP and not account-number
; TODO should not accept string as account-number
(defn insert-account
  "Inserts a new user into the accounts-map atom and returns the account
  number."
  [account-number accounts-map username email]
  (swap! accounts-map 
    assoc account-number (wrap-account username email))
  account-number)

; TODO username and email MUST be strings
; TODO accounts-map MUST be a MAP atom
(defn create-account
  "Creates a new account with the provided username and email values
  and inserts it into the accounts-map. If username and/or email are 
  nil, returns a failure map object (i.e. a JSON object). Otherwise, 
  returns a success map object with the account number in it."
  [accounts-map username email]
  (if (and accounts-map username email)
    (-> (generate-account-number accounts-map)
        (insert-account accounts-map username email)
        (fn/wrap-retval-success :account-number))
    (fn/retval-failure messages/MSG_0002)))