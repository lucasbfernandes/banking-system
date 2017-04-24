(ns banking-system.accounts.accounts-map
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

(defn wrap-operation
  "Wraps a new operation with the provided account-number, description,
  amount, date and type."
  [account-number description amount date type]
  {:account-number account-number
   :description description 
   :amount amount
   :date (helper/format-date date)
   :type type})

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

(defn get-operations
  "Retrieves the operations structure of a given account."
  [accounts-map account-number]
  ((@accounts-map account-number) :operations))

(defn set-operation
  "Updates atom to hold a new operation."
  [operation accounts-map account-number]
    (swap! 
      (get-operations accounts-map account-number)
      helper/insert-sorted operation
      helper/operations-comparator))

(defn insert-operation
  "Inserts a new operation (Credit or Debit) into an account in the accounts
  map. If the account does not exit or one of the values is nil, return failure."
  [accounts-map account-number description amount date type]
  (if (and accounts-map account-number description amount date
           type (@accounts-map account-number))
    (do
      (-> (wrap-operation account-number description amount date type)
    	  (set-operation accounts-map account-number))
      (helper/retval-success))
    (helper/retval-failure messages/MSG_0002)))

(defn get-operation-amount
  "Takes an operation as parameter and return its amount based on its type.
  Example: If an operation has an amount of 2000 and is a debit operation,
  then this function will return -2000."
  [operation]
  (if (= (operation :type) "C")
    (+ 0 (operation :amount))
    (- 0 (operation :amount))))

(defn retrieve-balance
  "Returns the current balance of an account. The balance is the sum of all
  operations until max-date."
  ([accounts-map account-number max-date]
    (retrieve-balance accounts-map account-number max-date 0 0.0))
  ([accounts-map account-number max-date pos balance]
    (if (and accounts-map account-number max-date
             pos balance (@accounts-map account-number))
      (let [operations @(get-operations accounts-map account-number)]
        (if (or (empty? operations) (= pos (count operations)))
          (helper/wrap-success balance :balance)
          (let [operation (nth operations pos)]
            (if (helper/date-before-equals? (operation :date) max-date)
              (retrieve-balance
                accounts-map account-number
                max-date (inc pos)
                (+ balance (get-operation-amount operation)))
              (helper/wrap-success balance :balance)))))
      (helper/retval-failure messages/MSG_0002))))

(defn update-statement-day
  "Adds one operation to a statement day."
  [statement operation]
  (let [date-str (helper/date-string (operation :date))]
    (assoc statement date-str
      (assoc (statement date-str) :operations
        (conj ((statement date-str) :operations)
          {:description (operation :description)
           :amount (operation :amount)
           :type (operation :type)})))))

(defn create-statement-day
  "Creates a statement structure for a given day."
  [statement operation]
  (let [date-str (helper/date-string (operation :date))]
    (assoc statement date-str
      (assoc {}
        :balance
        ((retrieve-balance
           accounts-map 
           (operation :account-number) 
           (operation :date)) 
         :balance)
        :operations []))))

(defn get-account-statement
  "Returns the bank statement of a given account between two specified dates."
  ([accounts-map account-number begin-date end-date]
    (get-account-statement accounts-map account-number begin-date end-date 0 {}))
  ([accounts-map account-number begin-date end-date pos statement]
    (if (and accounts-map account-number begin-date end-date
    	     pos statement (@accounts-map account-number))
      (let [operations @(get-operations accounts-map account-number)]
        (if (or (empty? operations) (= pos (count operations)))
          (assoc (helper/retval-success) :statement statement)
          (let [operation (nth operations pos)]
            (if (helper/is-date-between? (operation :date) begin-date end-date)
              (if (contains? statement (helper/date-string (operation :date)))
              	(get-account-statement
              	  accounts-map account-number
              	  begin-date end-date
              	  (inc pos) (update-statement-day statement operation))
                (get-account-statement
                  accounts-map account-number
                  begin-date end-date
                  (inc pos) (update-statement-day (create-statement-day statement operation) operation)))
              (get-account-statement
                accounts-map account-number 
                begin-date end-date
                (inc pos) statement)))))
      (helper/retval-failure messages/MSG_0002))))