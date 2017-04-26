(ns banking-system.account-management.statements
  (:require
    [banking-system.helpers.fn :as fn]
    [banking-system.helpers.validators :refer :all]
    [banking-system.settings.messages :as messages]
    [banking-system.account-management.operations
      :refer [get-operations get-operation-amount]]))

(defn get-account-balance
  "Returns the current balance of an account. The balance is the sum of all
  operations until max-date."
  [accounts-map account-number max-date]
  (try
    (is-atom-map? accounts-map)
    (is-integer-string? account-number)
    (is-date-string? max-date)
    (is-account-inside-map? account-number accounts-map)
    (loop [pos 0 balance 0.0]
      (let [operations @(get-operations accounts-map account-number)]
        (if (or (empty? operations) (= pos (count operations)))
          (fn/wrap-retval-success balance :balance)
          (let [operation (nth operations pos)]
            (if (fn/date-before-equals? (operation :date) (fn/format-date max-date))
              (recur (inc pos) (+ balance (get-operation-amount operation)))
              (fn/wrap-retval-success balance :balance))))))
    (catch Exception e
      (fn/retval-failure messages/MSG_0002))))

(defn create-day-statement
  "Creates a statement structure for a given day."
  [accounts-map statements-map operation]
  (is-atom-map? accounts-map)
  (is-map? statements-map)
  (is-map? operation)
  (let [date-str (fn/date-string (operation :date))]
    (assoc statements-map date-str
      (assoc {}
        :balance
        ((get-account-balance
           accounts-map 
           (operation :account-number) 
           (fn/date-string (operation :date))) 
         :balance)
        :operations []))))

(defn add-operation-day-statement
  "Adds one operation to a statement day."
  [statements-map operation]
  (is-map? statements-map)
  (is-map? operation)
  (let [date-str (fn/date-string (operation :date))]
    (assoc statements-map date-str
      (assoc (statements-map date-str) :operations
        (conj ((statements-map date-str) :operations)
          {:description (operation :description)
           :amount (operation :amount)
           :type (operation :type)})))))

(defn get-account-statement
  "Returns the bank statement of a given account between two specified dates."
  [accounts-map account-number begin-date end-date]
  (try
    (is-atom-map? accounts-map)
    (is-integer-string? account-number)
    (is-date-string? begin-date)
    (is-date-string? end-date)
    (is-account-inside-map? account-number accounts-map)
    (loop [pos 0 statements-map {}]
      (let [operations @(get-operations accounts-map account-number)]
        (if (or (empty? operations) (= pos (count operations)))
          (fn/wrap-retval-success statements-map :statement)
          (let [operation (nth operations pos)]
            (if (fn/is-date-between? (operation :date) (fn/format-date begin-date) (fn/format-date end-date))
              (if (contains? statements-map (fn/date-string (operation :date)))
                (recur (inc pos) (add-operation-day-statement statements-map operation))
                (recur (inc pos) 
                       (-> (create-day-statement accounts-map statements-map operation)
                           (add-operation-day-statement operation))))
              (if (fn/date-before? (fn/format-date end-date) (operation :date))
                (fn/wrap-retval-success statements-map :statement)
                (recur (inc pos) statements-map)))))))
    (catch Exception e
      (fn/retval-failure messages/MSG_0002))))

(defn get-periods-of-debt
 ""
 []
 (try))