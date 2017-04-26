(ns banking-system.helpers.fn
  (:require
    [clj-time.format :as time-format]
    [clj-time.core :as time]
    [banking-system.settings.messages :as messages]
    [banking-system.helpers.validators :refer :all]))

(defn insert-sorted
  "Takes a vector as parameter and inserts the element in a
  position that satisfies the comparator function order.
  This function will take O(N) in the worst case."
  [vect elem compar]
  (is-vector? vect)
  (is-function? compar)
  (loop [pos 0]
    (if (empty? vect)
      (conj vect elem)
      (if (compar elem (nth vect pos))
        (let [[before after] (split-at pos vect)]
          (vec (concat before [elem] after)))
        (let [length (count vect)]
          (if (= pos (dec length))
            (conj vect elem)
            (recur (inc pos))))))))

(defn date-equals? 
  "Takes two date objects as parameters and checks whether both
  are equal (i.e same day)."
  [a b]
  (is-date-object? a)
  (is-date-object? b)
  (time/equal? a b))

(defn date-before?
  "Takes two date objects as parameters and checks whether the
  first one comes before the second."
  [a b]
  (is-date-object? a)
  (is-date-object? b)
  (time/before? a b))

(defn date-before-equals?
  "Takes two date objects as parameters and checks whether the
  first one comes before or is equal to the second one."
  [a b]
  (is-date-object? a)
  (is-date-object? b)
  (or (date-before? a b) (date-equals? a b)))

(defn is-date-between?
  "Takes three date objects and asserts whether the first one is
  between the other two."
  [date date-begin date-end]
  (is-date-object? date)
  (is-date-object? date-begin)
  (is-date-object? date-end)
  (is-date-before-equals? date-begin date-end)
  (and (or (date-before? date-begin date)
           (date-equals? date-begin date))
       (or (date-before? date date-end)
           (date-equals? date date-end))))

(defn get-today-date
  "Returns the current date of the system, without time."
  []
  (time/today-at 00 00))

(defn is-today?
  "Takes one date object and checks whether it equals today."
  [date]
  (is-date-object? date)
  (date-equals? date (get-today-date)))

(defn before-today?
  "Takes one date object and checks whether it comes before today."
  [date]
  (is-date-object? date)
  (date-before? date (get-today-date)))

(defn format-date
  "Takes a string in the format yyyy-mm-dd and converts it to a
  date object."
  [date-string]
  (is-date-string? date-string)
  (time-format/parse (time-format/formatters :date) date-string))

(defn date-string
  "Takes a date object and converts it to a human readable string.
  Format: yyyy-mm-dd."
  [date-object]
  (is-date-object? date-object)
  (time-format/unparse (time-format/formatters :date) date-object))

(defn get-random-between
  "Takes two integers as parameters and return a random integer number
  between both (inclusive)."
  [begin end]
  (is-integer? begin)
  (is-integer? end)
  (is-integer-less-or-equal? begin end)
  (+ (rand-int (- (inc end) begin)) begin))

(defn get-json-param
  "Returns the value of the specified parameter in the request."
  [request param]
  (is-map? request)
  (get-in request [:body param]))

(defn retval-success 
  "Default return map for successful executions."
  []
  {:status true :message messages/MSG_0001})

(defn wrap-retval-success
  "Wraps retval success with another key/value pair."
  [value value-key]
  (assoc (retval-success) value-key value))

(defn retval-failure 
  "Default return map for failed executions. It accepts a message
  is meant to explain what happened."
  [message]
  {:status false :message message})