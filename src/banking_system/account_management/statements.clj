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
    (is-date-before-equals? (fn/format-date begin-date) (fn/format-date end-date))
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

(defn get-statement
  "Returns the statement of a given position"
  [st-map pos]
  (is-map? st-map)
  (is-integer? pos)
  (is-pos-valid? (keys st-map) pos)
  (st-map (nth (keys st-map) pos)))

(defn wrap-debt-period
  "Wraps a new debt period  based on the provided balance, begin date and
  end date."
  [balance begin-date end-date]
  (is-number? balance)
  (is-negative? balance)
  (is-date-string? begin-date)
  (is-date-string? end-date)
  (is-date-before-equals? (fn/format-date begin-date) (fn/format-date end-date))
  {:principal (* -1.0 balance)
    :start begin-date
    :end end-date})

(defn wrap-last-debt-period
  "Wraps the last debt period based on the provided balance and begin date."
  [balance begin-date]
  (is-number? balance)
  (is-negative? balance)
  (is-date-string? begin-date)
  {:principal (* -1.0 balance)
   :start begin-date})

(defn insert-debt-period
  "Receives a debt-period vector and inserts a new debt period in it."
  [debt-vector debt-period]
  (is-vector? debt-vector)
  (is-map? debt-period)
  (conj debt-vector debt-period))

(defn get-periods-of-debt
 "Returns a map with all periods of time that the account had a negative balance."
 [accounts-map account-number begin-date end-date]
 (try
    (is-atom-map? accounts-map)
    (is-integer-string? account-number)
    (is-date-string? begin-date)
    (is-date-string? end-date)
    (is-account-inside-map? account-number accounts-map)
    (is-date-before-equals? (fn/format-date begin-date) (fn/format-date end-date))
    (let [st-map ((get-account-statement accounts-map account-number begin-date end-date) :statement)
          keys-len (count (keys st-map))]
      (loop [pos 0 debt-vector []]
        (if (= pos keys-len)
          (fn/wrap-retval-success debt-vector :debt-periods)
          (let [current-statement (get-statement st-map pos)
                current-balance (current-statement :balance)]
            (if (neg? current-balance)
              (if (< pos (dec keys-len))
                (recur (inc pos) (insert-debt-period
                                   debt-vector
                                   (wrap-debt-period current-balance (nth (keys st-map) pos)
                                     (fn/date-string (fn/date-day-before 
                                                  (fn/format-date (nth (keys st-map) (inc pos))))))))
                (recur (inc pos) (insert-debt-period
                                   debt-vector
                                   (wrap-last-debt-period
                                     current-balance
                                     (nth (keys st-map) pos)))))
              (recur (inc pos) debt-vector))))))
    (catch Exception e
      (fn/retval-failure (.getMessage e)))))