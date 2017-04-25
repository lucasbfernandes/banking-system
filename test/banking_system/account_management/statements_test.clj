(ns banking-system.account-management.statements-test
  (:require
    [clojure.test :refer :all]
    [banking-system.helpers.fn :refer :all]
    [banking-system.account-management.operations :refer :all]
    [banking-system.account-management.accounts :refer :all]
    [banking-system.account-management.statements :refer :all]))

; accounts-map account-number max-date
(deftest get-account-balance-test
  (testing "illegal nil arguments"
    (is (false? ((get-account-balance nil nil nil) :status))))
  (testing "illegal date string arguments"
    (is (false? ((get-account-balance "2017-02-03" "2017-03-03" "2017-03-03") :status))))
  (testing "illegal number arguments"
    (is (false? ((get-account-balance 2017.2 2017 2000) :status))))
  (testing "illegal regular string parameters"
    (is (false? ((get-account-balance "ABCD" "ABCD" "DCBA") :status))))
  (testing "illegal arguments - accounts-map must be a map atom"
    (is (false? ((get-account-balance "ABCD" 123445 "2017-03-04") :status))))
  (testing "illegal arguments - accounts-map must be a map atom"
    (is (false? ((get-account-balance {} 123445 "2017-03-04") :status))))
  (testing "illegal arguments - account-number must be an integer"
    (is (false? ((get-account-balance (atom {"ABCD" {}}) "ABCD" "2017-03-04") :status))))
  (testing "illegal arguments - max-date must be a date string"
    (is (false? ((get-account-balance (atom {123456 {}}) 123456 (format-date "2017-03-04")) :status))))
  (testing "no operation correct balance"
    (is (= ((get-account-balance 
              (atom {123456 (wrap-account "a" "a")})
              123456 
              (date-string (get-today-date))) :balance)
            0.0)))
  (testing "one operation correct balance"
    (is (= ((get-account-balance
             (insert-operation
               (wrap-operation 123456 "a" 20 "2017-05-05" "D")
               (atom {123456 (wrap-account "a" "a")})
               123456)
             123456
             (date-string (get-today-date))) :balance)
            20.0))))
