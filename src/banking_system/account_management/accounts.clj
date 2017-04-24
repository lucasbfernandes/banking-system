(ns banking-system.account-management.accounts
  (:require
    [banking-system.helpers.fn :as helper]
    [banking-system.settings.messages :as messages]))

(def accounts-map (atom {}))

(defn wrap-account
  "Wraps a new account with the provided username and email values"
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

(defn remove-account
  "Removes an account from the account map. Returns account number."
  [account-number accounts-map]
  (swap! accounts-map
    dissoc account-number)
  account-number)

(defn set-account
  "Updates atom to hold the new user. Returns account number."
  [account-number accounts-map username email]
  (swap! accounts-map 
    assoc account-number (wrap-account username email))
  account-number)

(defn insert-account
  "Inserts a new account with the provided username and email values
  into the accounts-map. If username and/or email are nil, return 
  failure. Otherwise, return JSON with account number."
  [accounts-map username email]
  (if (and accounts-map username email)
    (-> (generate-account-number accounts-map)
        (set-account accounts-map username email)
        (helper/wrap-success :account-number))
    (helper/retval-failure messages/MSG_0002)))