(ns banking-system.helpers.validators
  (:require
    [clj-time.format :as time-format]
    [clj-time.core :as time]))

(defn is-integer?
  "Checks whether elem is a integer. If yes, return true, if not, throw Exception."
  [elem]
  (if (integer? elem)
    true
    (throw (Exception. (str elem " is not a integer.")))))

(defn is-integer-less-or-equal?
  "Checks whether an integer is less or equal to another. If yes, return true, if not,
  throw Exception."
  [a b]
  (if (<= a b)
    true
    (throw (Exception. (str a " must be less or equal to " b ".")))))

(defn is-number?
  "Checks whether elem is a number (i.e. integer or float). If yes, return true,
  if not, throw Exception."
  [elem]
  (if (or (integer? elem) (float? elem))
    true
    (throw (Exception. (str elem " is not a number.")))))

(defn is-string?
  "Checks whether elem is a string. If yes, return true, if not, throw Exception."
  [elem]
  (if (string? elem)
    true
    (throw (Exception. (str elem " is not a string.")))))

(defn is-integer-string?
  "Checks whether elem is an integer string. If yes, return true, if not, throw Exception."
  [elem]
  (if (and (is-string? elem) (Integer. elem))
    true
    (throw (Exception. (str elem " is not an integer string.")))))

(defn is-email?
  "Checks whether elem is an email. If yes, return true, if not, throw Exception."
  [elem]
  (if (and (is-string? elem) (re-matches #".+\@.+\..+" elem))
    true
    (throw (Exception. (str elem " is not an email.")))))

(defn is-date-string?
  "Checks whether elem is a date-string. If yes, return true, if not, throw Exception."
  [elem]
  (if (and (is-string? elem)
      (time-format/parse (time-format/formatters :date) elem)
      (re-matches #"\d{4}-\d{2}-\d{2}" elem))
    true
    (throw (Exception. (str elem " is not a date-string.")))))

(defn is-date-object?
  "Checks whether elem is a date-object. If yes, return true, if not, throw Exception."
  [elem]
  (if (and elem (is-date-string? (time-format/unparse (time-format/formatters :date) elem)))
    true
    (throw (Exception. (str elem " is not a date-object.")))))

(defn is-date-before-equals?
  "Checks whether a date object comes before another. If yes, return true, if not,
  throw Exception."
  [a b]
  (if (or (time/before? a b) (time/equal? a b))
    true
    (throw (Exception. (str a " must come before " b ".")))))

(defn is-map?
  "Checks whether elem is a map. If yes, return true, if not, throw Exception."
  [elem]
  (if (map? elem)
    true
    (throw (Exception. (str elem " is not a map.")))))

(defn is-atom-map?
  "Checks whether elem is a atom-map. If yes, return true, if not, throw Exception."
  [elem]
  (if (map? @elem)
    true
    (throw (Exception. (str elem " is not a atom-map.")))))

(defn is-vector?
  "Checks whether elem is a vector. If yes, return true, if not, throw Exception."
  [elem]
  (if (vector? elem)
    true
    (throw (Exception. (str elem " is not a vector.")))))

(defn is-function?
  "Checks whether elem is a function. If yes, return true, if not, throw Exception."
  [elem]
  (if (fn? elem)
    true
    (throw (Exception. (str elem " is not a function.")))))

(defn is-atom-vector?
  "Checks whether elem is a atom-vector. If yes, return true, if not, throw Exception."
  [elem]
  (if (vector? @elem)
    true
    (throw (Exception. (str elem " is not a atom-vector.")))))

(defn is-account-inside-map?
  "Checks whether the account number is inside a atom map. If yes, return true, if not,
  throw Exception."
  [account-number accounts-map]
  (is-integer-string? account-number)
  (is-atom-map? accounts-map)
  (if (@accounts-map account-number)
    true
    (throw (Exception. (str "Account " account-number " is not inside the map.")))))



