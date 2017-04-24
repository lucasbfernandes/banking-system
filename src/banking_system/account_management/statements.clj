(ns banking-system.account-management.statements
  (:require
    [banking-system.helpers.fn :as fn]
    [banking-system.settings.messages :as messages]
    [banking-system.account-management.operations
      :refer [get-operations get-operation-amount]]))

(defn get-account-balance
  "Returns the current balance of an account. The balance is the sum of all
  operations until max-date."
  [accounts-map account-number max-date]
  (if (and accounts-map account-number max-date
           (@accounts-map account-number))
    (loop [pos 0 balance 0.0]
      (let [operations @(get-operations accounts-map account-number)]
        (if (or (empty? operations) (= pos (count operations)))
          (fn/wrap-success balance :balance)
          (let [operation (nth operations pos)]
            (if (fn/date-before-equals? (operation :date) max-date)
              (recur (inc pos) (+ balance (get-operation-amount operation)))
              (fn/wrap-success balance :balance))))))
    (fn/retval-failure messages/MSG_0002)))

(defn update-statement-day
  "Adds one operation to a statement day."
  [statement operation]
  (let [date-str (fn/date-string (operation :date))]
    (assoc statement date-str
      (assoc (statement date-str) :operations
        (conj ((statement date-str) :operations)
          {:description (operation :description)
           :amount (operation :amount)
           :type (operation :type)})))))

(defn create-statement-day
  "Creates a statement structure for a given day."
  [accounts-map statement operation]
  (let [date-str (fn/date-string (operation :date))]
    (assoc statement date-str
      (assoc {}
        :balance
        ((get-account-balance
           accounts-map 
           (operation :account-number) 
           (operation :date)) 
         :balance)
        :operations []))))

(defn get-account-statement
  "Returns the bank statement of a given account between two specified dates."
  [accounts-map account-number begin-date end-date]
  (if (and accounts-map account-number begin-date
           end-date (@accounts-map account-number))
    (loop [pos 0 statement {}]
      (let [operations @(get-operations accounts-map account-number)]
        (if (or (empty? operations) (= pos (count operations)))
          (fn/wrap-success statement :statement)
          (let [operation (nth operations pos)]
            (if (fn/is-date-between? (operation :date) begin-date end-date)
              (if (contains? statement (fn/date-string (operation :date)))
                (recur (inc pos) (update-statement-day statement operation))
                (recur (inc pos) 
                       (-> (create-statement-day accounts-map statement operation)
                           (update-statement-day operation))))
              (if (fn/date-before? end-date (operation :date))
                (fn/wrap-success statement :statement)
                (recur (inc pos) statement)))))))
    (fn/retval-failure messages/MSG_0002)))