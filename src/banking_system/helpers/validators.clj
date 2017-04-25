(ns banking-system.helpers.validators
  (:require
    [banking-system.helpers.fn :refer :all]))

(defn is-integer?
  "Checks whether elem is a integer. If yes, return true, if not, throw Exception."
  [elem]
  (if (integer? elem)
    true
    (throw (Exception. (str elem " is not a integer.")))))

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
  (if (and (is-string? elem) (format-date elem))
    true
    (throw (Exception. (str elem " is not a date-string.")))))

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

(defn is-atom-vector?
  "Checks whether elem is a atom-vector. If yes, return true, if not, throw Exception."
  [elem]
  (if (vector? @elem)
    true
    (throw (Exception. (str elem " is not a atom-vector.")))))




