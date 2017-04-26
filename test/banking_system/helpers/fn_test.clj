(ns banking-system.helpers.fn-test
  (:require
    [clojure.test :refer :all]
    [banking-system.helpers.fn :refer :all]))

(deftest insert-sorted-test
  (testing "illegal date string arguments"
    (is (thrown? Exception (insert-sorted [] "2034-03-20" "2030-04-01"))))
  (testing "illegal number arguments"
    (is (thrown? Exception (insert-sorted [] 2000 20))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (insert-sorted [] "aaa" "ccc"))))
  (testing "illegal non vector argument"
    (is (thrown? Exception (insert-sorted "aaa" "ABCD" 2000))))
  (testing "insert in emtpy vector"
    (is (= (nth (insert-sorted [] 1 <) 0) 1)))
  (testing "insert in the first position"
    (is (= (nth (insert-sorted [0] 1 >) 0) 1)))
  (testing "insert in the last position"
    (is (= (nth (insert-sorted [2] 1 >) 1) 1)))
  (testing "insert in the middle"
    (is (= (nth (insert-sorted [1 2 6 7] 5 <) 2) 5)))
  (testing "insert with custom function"
    (is (= (nth (insert-sorted [(format-date "2017-02-03") (format-date "2017-03-04")] 
                                (format-date "2017-02-04") date-before?) 1) 
           (format-date "2017-02-04")))))

(deftest date-equals-test
  (testing "illegal date string arguments"
    (is (thrown? Exception (date-equals? "2017-03-03" "2017-03-03"))))
  (testing "illegal number arguments"
    (is (thrown? Exception (date-equals? 2017.2 2017))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (date-equals? "ABCD" "ABCD"))))
  (testing "illegal combination of date object/date string"
    (is (thrown? Exception (date-equals? (format-date "2017-03-03") "2017-03-03"))))
  (testing "illegal combination of date object/date string"
    (is (thrown? Exception (date-equals? "2017-03-03" (format-date "2017-03-03")))))
  (testing "valid arguments false assertion"
    (is (false? (date-equals? (format-date "2017-03-04") (format-date "2017-05-06")))))
  (testing "valid arguments valid assertion"
    (is (true? (date-equals? (format-date "2017-03-04") (format-date "2017-03-04"))))))

(deftest date-before-test
  (testing "illegal date string arguments"
    (is (thrown? Exception (date-before? "2017-02-03" "2017-03-03"))))
  (testing "illegal number arguments"
    (is (thrown? Exception (date-before? 2017.2 2017))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (date-before? "ABCD" "ABCD"))))
  (testing "illegal combination of date object/date string"
    (is (thrown? Exception (date-before? (format-date "2017-03-03") "2017-03-03"))))
  (testing "illegal combination of date object/date string"
    (is (thrown? Exception (date-before? "2017-03-03" (format-date "2017-03-03")))))
  (testing "valid arguments false assertion"
    (is (false? (date-before? (format-date "2017-08-04") (format-date "2017-05-06")))))
  (testing "valid arguments false assertion"
    (is (false? (date-before? (format-date "2017-08-04") (format-date "2017-08-04")))))
  (testing "valid arguments valid assertion"
    (is (true? (date-before? (format-date "2017-03-04") (format-date "2017-03-07"))))))

(deftest date-before-equals-test
  (testing "illegal date string arguments"
    (is (thrown? Exception (date-before-equals? "2017-02-03" "2017-03-03"))))
  (testing "illegal number arguments"
    (is (thrown? Exception (date-before-equals? 2017.2 2017))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (date-before-equals? "ABCD" "ABCD"))))
  (testing "illegal combination of date object/date string"
    (is (thrown? Exception (date-before-equals? (format-date "2017-03-03") "2017-03-03"))))
  (testing "illegal combination of date object/date string"
    (is (thrown? Exception (date-before-equals? "2017-03-03" (format-date "2017-03-03")))))
  (testing "valid arguments false assertion"
    (is (false? (date-before-equals? (format-date "2017-08-04") (format-date "2017-05-06")))))
  (testing "valid arguments valid assertion"
    (is (true? (date-before-equals? (format-date "2017-08-04") (format-date "2017-08-04")))))
  (testing "valid arguments valid assertion"
    (is (true? (date-before-equals? (format-date "2017-03-04") (format-date "2017-03-07"))))))

(deftest is-date-between-test
  (testing "illegal date string arguments"
    (is (thrown? Exception (is-date-between? "2017-02-03" "2017-03-03" "2017-03-03"))))
  (testing "illegal number arguments"
    (is (thrown? Exception (is-date-between? 2017.2 2017 2000))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (is-date-between? "ABCD" "ABCD" "DCBA"))))
  (testing "illegal combination of date object/date string"
    (is (thrown? Exception (is-date-between? (format-date "2017-03-03") "2000-03-04" "2017-03-03"))))
  (testing "illegal combination of date object/date string"
    (is (thrown? Exception (is-date-between? "2012-02-03" (format-date "2017-03-03") "2017-03-03"))))
  (testing "illegal combination of date object/date string"
    (is (thrown? Exception (is-date-between? "2012-02-03" "2017-03-03" (format-date "2017-03-03")))))
  (testing "date begin must come before date end"
    (is (thrown? Exception (is-date-between? (format-date "2017-02-02") (format-date "2017-02-02") (format-date "2017-02-01")))))
  (testing "valid arguments false assertion"
    (is (false? (is-date-between? (format-date "2017-08-04") (format-date "2017-05-01") (format-date "2017-05-03")))))
  (testing "valid arguments valid assertion"
    (is (true? (is-date-between? (format-date "2017-08-04") (format-date "2017-08-01") (format-date "2017-08-20")))))
  (testing "valid arguments valid assertion"
    (is (true? (is-date-between? (format-date "2017-08-04") (format-date "2017-08-01") (format-date "2017-08-04")))))
  (testing "valid arguments valid assertion"
    (is (true? (is-date-between? (format-date "2017-03-04") (format-date "2017-03-04") (format-date "2017-03-20"))))))

(deftest is-today-test
  (testing "illegal date string arguments"
    (is (thrown? Exception (is-today? "2017-02-03"))))
  (testing "illegal number arguments"
    (is (thrown? Exception (is-today? 2017.2))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (is-today? "ABCD")))))

(deftest before-today-test
  (testing "illegal date string arguments"
    (is (thrown? Exception (before-today? "2017-02-03"))))
  (testing "illegal number arguments"
    (is (thrown? Exception (before-today? 2017.2))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (before-today? "ABCD")))))

(deftest format-date-test
  (testing "illegal number arguments"
    (is (thrown? Exception (format-date 2017.2))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (format-date "200-00-000-"))))
  (testing "illegal date string parameters"
    (is (thrown? Exception (format-date "2017-50-02"))))
  (testing "valid arguments valid assertion"
    (is (= (type (format-date "2017-02-03")) org.joda.time.DateTime))))

(deftest date-string-test
  (testing "illegal nil arguments",
    (is (thrown? Exception (date-string nil))))
  (testing "illegal date string arguments"
    (is (thrown? Exception (date-string "2017-02-03"))))
  (testing "illegal number arguments"
    (is (thrown? Exception (date-string 2017.2))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (date-string "ABCD"))))
  (testing "valid arguments valid assertion"
    (is (= (date-string (format-date "2017-03-03")) "2017-03-03"))))

(deftest get-random-between-test
  (testing "illegal date string arguments"
    (is (thrown? Exception (get-random-between "2017-02-03" "2000-02-03"))))
  (testing "illegal number arguments"
    (is (thrown? Exception (get-random-between 2017.2 2008003.002))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (get-random-between "ABCD" "aaa"))))
  (testing "begin must be less than end"
    (is (thrown? Exception (get-random-between 2000 1000))))
  (testing "valid arguments valid assertion"
    (is (every? true? 
          (map (fn [x] (let [random (get-random-between 0 10)]
                         (and (<= random 10) (>= random 0)))) 
               (range 10000))))))

(deftest get-json-param-test
  (testing "illegal date string arguments"
    (is (thrown? Exception (get-json-param "2017-02-03"))))
  (testing "illegal number arguments"
    (is (thrown? Exception (get-json-param 2017.2))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (get-json-param "ABCD"))))
  (testing "valid parameter nil assertion"
    (is (nil? (get-json-param {} :a))))
  (testing "valid arguments valid assertion"
    (is (= (get-json-param {:body {:a 2}} :a) 2))))

(deftest wrap-retval-success-test
  (testing "map contains key/val pair"
    (is (= ((wrap-retval-success "2010-02-03" :date) :date) "2010-02-03"))))

(deftest retval-failure-test
  (testing "map contains correct :message"
    (is (= ((retval-failure "my message") :message) "my message"))))