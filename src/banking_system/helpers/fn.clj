(ns banking-system.helpers.fn
  (:require
    [clj-time.format :as time-format]
    [clj-time.core :as time]
    [banking-system.settings.messages :as messages]))

(defn insert-sorted
  "Takes a vector as parameter and inserts the element in a
  position that satisfies the comparator function order.
  This function will take O(N) in the worst case."
  ([vect elem compar] (insert-sorted vect elem compar 0))
  ([vect elem compar pos]
    (if (empty? vect)
      (conj vect elem)
      (if (compar elem (nth vect pos))
        (let [[before after] (split-at pos vect)]
          (vec (concat before [elem] after)))
        (let [length (count vect)]
          (if (= pos (dec length))
            (conj vect elem)
            (insert-sorted vect elem compar (inc pos))))))))

(defn date-equals? 
  "Takes two date objects as parameters and checks whether both
  are equal (i.e same day)."
  [a b]
  (time/equal? a b))

(defn date-before?
  "Takes two date objects as parameters and checks whether the
  first one comes before the second."
  [a b]
  (time/before? a b))

(defn date-before-equals?
  "Takes two date objects as parameters and checks whether the
  first one comes before or is equal to the second one."
  [a b]
  (or (date-before? a b) (date-equals? a b)))

; TODO date begin must come before date-end
(defn is-date-between?
  "Takes three date objects and asserts whether the first one is
  between the other two."
  [date date-begin date-end]
  (and (or (date-before? date-begin date)
           (date-equals? date-begin date))
       (or (date-before? date date-end)
           (date-equals? date date-end))))

; TODO MOVE THIS TO operations.clj - IT DOES NOT BELONG HERE!!!
(defn operations-comparator
  "Takes two operations as parameters and checks whether the
  first one comes before the second in regards to their date."
  [a b]
  (date-before? (a :date) (b :date)))

(defn get-today-date
  "Returns the current date of the system, without time."
  []
  (time/today-at 00 00))

(defn is-today?
  "Takes one date object and checks whether it equals today."
  [date]
  (date-equals? date (get-today-date)))

(defn before-today?
  "Takes one date object and checks whether it comes before today."
  [date]
  (date-before? date (get-today-date)))

(defn format-date
  "Takes a string in the format yyyy-mm-dd and converts it to a
  date object."
  [date-string]
  (time-format/parse (time-format/formatters :date) date-string))

(defn date-string
  "Takes a date object and converts it to a human readable string.
  Format: yyyy-mm-dd."
  [date-object]
  (time-format/unparse (time-format/formatters :date) date-object))

(defn get-random-between
  "Takes two integers as parameters and return a random integer number
  between both (inclusive)."
  [begin end]
  (+ (rand-int (- (inc end) begin)) begin))

(defn get-json-param
  "Returns the value of the specified parameter in the request."
  [request param]
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