(ns banking-system.account-management.statements-test
  (:require
    [clojure.test :refer :all]
    [banking-system.helpers.fn :refer :all]
    [banking-system.account-management.operations :refer :all]
    [banking-system.account-management.accounts :refer :all]
    [banking-system.account-management.statements :refer :all]))

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
  (testing "illegal arguments - accounts-map must have account-number as a key"
    (is (false? ((get-account-balance (atom {123456 {}}) 654321 (date-string (get-today-date))) :status))))
  (testing "no operation correct balance debit"
    (is (= ((get-account-balance 
              (generate-dummy-accounts-map 123456 0 10 "D") 123456 (date-string (get-today-date))) :balance) 0.0)))
  (testing "one operation correct balance debit"
    (is (= ((get-account-balance
              (generate-dummy-accounts-map 123456 1 10 "D") 123456 (date-string (get-today-date))) :balance) -10.0)))
  (testing "two operations correct balance debit"
    (is (= ((get-account-balance
              (generate-dummy-accounts-map 123456 2 45 "D") 123456 (date-string (get-today-date))) :balance) -90.0)))
  (testing "no operation correct balance credit"
    (is (= ((get-account-balance 
              (generate-dummy-accounts-map 123456 0 10 "C") 123456 (date-string (get-today-date))) :balance) 0.0)))
  (testing "one operation correct balance credit"
    (is (= ((get-account-balance
              (generate-dummy-accounts-map 123456 1 10.5 "C") 123456 (date-string (get-today-date))) :balance) 10.5)))
  (testing "two operations correct balance credit"
    (is (= ((get-account-balance
              (generate-dummy-accounts-map 123456 2 45.2 "C") 123456 (date-string (get-today-date))) :balance) 90.4))))

; accounts-map statements-map operation
(deftest create-day-statement-test
  (testing "illegal date string arguments"
    (is (thrown? Exception (create-day-statement "2017-03-03" "2017-03-03" "2017-03-03"))))
  (testing "illegal number arguments"
    (is (thrown? Exception (create-day-statement 2017.2 2017 2017.2))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (create-day-statement "ABCD" "ABCD" "ABCD"))))
  (testing "illegal arguments - accounts-map must be a atom map"
    (is (thrown? Exception (create-day-statement {} {} {}))))
  (testing "illegal arguments - accounts-map must be a atom map"
    (is (thrown? Exception (create-day-statement (atom []) {} {}))))
  (testing "illegal arguments - statements-map must be a map"
    (is (thrown? Exception (create-day-statement (atom {}) [] {}))))
  (testing "illegal arguments - operation must be a map"
    (is (thrown? Exception (create-day-statement (atom {}) {} []))))
  (testing "output - must have the right date, i.e. the operation date"
    (is (contains? (create-day-statement 
                     (atom {123456 (wrap-account "teste" "teste")})
                     {}
                     (wrap-operation 123456 "teste" 200 "2017-03-04" "D")) 
                   "2017-03-04")))
  (testing "output - date must have a map with balance key with value 0.0"
    (is (= (((create-day-statement 
               (atom {123456 (wrap-account "teste" "teste")}) {} (wrap-operation 123456 "teste" 200 "2017-03-04" "D"))
             "2017-03-04")
             :balance) 0.0)))
  (testing "output - date must have a map with empty operations vector"
    (is (= (((create-day-statement 
               (atom {123456 (wrap-account "teste" "teste")}) {} (wrap-operation 123456 "teste" 200 "2017-03-04" "D"))
             "2017-03-04")
             :operations) []))))









