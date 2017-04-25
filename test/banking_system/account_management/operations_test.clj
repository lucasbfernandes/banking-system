(ns banking-system.account-management.operations-test
  (:require
    [clojure.test :refer :all]
    [banking-system.helpers.fn :refer [date-equals? format-date]]
    [banking-system.account-management.operations :refer :all]))

(deftest wrap-operation-test
  (testing "account contains correct account-number"
    (is (= ((wrap-operation 123456 "description" 2000 "2016-04-04" "D") :account-number) 123456)))
  (testing "account contains correct description"
    (is (= ((wrap-operation 123456 "description" 2000 "2016-04-04" "D") :description) "description")))
  (testing "account contains correct amount"
    (is (= ((wrap-operation 123456 "description" 2000 "2016-04-04" "D") :amount) 2000)))
  (testing "account contains correct date"
    (is (date-equals? ((wrap-operation 123456 "description" 2000 "2016-04-04" "D") :date) (format-date "2016-04-04"))))
  (testing "account contains correct type"
    (is (= ((wrap-operation 123456 "description" 2000 "2016-04-04" "D") :type) "D"))))

(deftest get-operations-test
  (testing "illegal date string arguments"
    (is (thrown? Exception (get-operations "2017-02-03" "2017-03-03"))))
  (testing "illegal number arguments"
    (is (thrown? Exception (get-operations 2017.2 2017))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (get-operations "ABCD" "ABCD"))))
  (testing "illegal arguments - account number must be a number"
    (is (thrown? Exception (get-operations (atom {}) "aaaaa"))))
  (testing "illegal arguments - accounts-map must me an atom map"
    (is (thrown? Exception (get-operations {} "aaaaa"))))
  (testing "illegal arguments - accounts-map must me an atom map"
    (is (thrown? Exception (get-operations (atom []) "aaaaa"))))
  (testing "operations key will be returned if atom has it"
    (is (not (nil? (get-operations (atom {123456 {:operations []}}) 123456))))))