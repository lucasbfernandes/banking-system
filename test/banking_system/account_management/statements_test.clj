(ns banking-system.account-management.statements-test
  (:require
    [clojure.test :refer :all]
    [banking-system.helpers.fn :refer :all]
    [banking-system.settings.constants :refer :all]
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
    (is (false? ((get-account-balance "ABCD" "123445" "2017-03-04") :status))))
  (testing "illegal arguments - accounts-map must be a map atom"
    (is (false? ((get-account-balance {} 123445 "2017-03-04") :status))))
  (testing "illegal arguments - account-number must be an integer"
    (is (false? ((get-account-balance (atom {"ABCD" (wrap-account "name" "e@e.c")}) "ABCD" "2017-03-04") :status))))
  (testing "illegal arguments - max-date must be a date string"
    (is (false? ((get-account-balance (atom {"123456" (wrap-account "name" "e@e.c")}) "123456" (format-date "2017-03-04")) :status))))
  (testing "illegal arguments - accounts-map must have account-number as a key"
    (is (false? ((get-account-balance (atom {"123456" (wrap-account "name" "e@e.c")}) "654321" (date-string (get-today-date))) :status))))
  (testing "no operation correct balance debit"
    (is (= ((get-account-balance 
              (generate-dummy-accounts-map "123456" 0 10 "D") "123456" (date-string (get-today-date))) :balance) 0.0)))
  (testing "one operation correct balance debit"
    (is (= ((get-account-balance
              (generate-dummy-accounts-map "123456" 1 10 "D") "123456" (date-string (get-today-date))) :balance) -10.0)))
  (testing "two operations correct balance debit"
    (is (= ((get-account-balance
              (generate-dummy-accounts-map "123456" 2 45 "D") "123456" (date-string (get-today-date))) :balance) -90.0)))
  (testing "no operation correct balance credit"
    (is (= ((get-account-balance 
              (generate-dummy-accounts-map "123456" 0 10 "C") "123456" (date-string (get-today-date))) :balance) 0.0)))
  (testing "one operation correct balance credit"
    (is (= ((get-account-balance
              (generate-dummy-accounts-map "123456" 1 10.5 "C") "123456" (date-string (get-today-date))) :balance) 10.5)))
  (testing "two operations correct balance credit"
    (is (= ((get-account-balance
              (generate-dummy-accounts-map "123456" 2 45.2 "C") "123456" (date-string (get-today-date))) :balance) 90.4))))

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
                     (atom {"123456" (wrap-account "teste" "teste@teste.com")})
                     {}
                     (wrap-operation "123456" "teste" 200 "2017-03-04" "D")) 
                   "2017-03-04")))
  (testing "output - day must have a map where balance has value 0.0"
    (is (= (((create-day-statement 
               (atom {"123456" (wrap-account "teste" "teste@teste.com")}) {} (wrap-operation "123456" "teste" 200 "2017-03-04" "D"))
             "2017-03-04")
             :balance) 0.0)))
  (testing "output - day must have a map with an empty operations vector"
    (is (= (((create-day-statement 
               (atom {"123456" (wrap-account "teste" "teste@teste.com")}) {} (wrap-operation "123456" "teste" 200 "2017-03-04" "D"))
             "2017-03-04")
             :operations) []))))

(deftest add-operation-day-statement-test
  (testing "illegal date string arguments"
    (is (thrown? Exception (add-operation-day-statement "2017-03-03" "2017-03-03"))))
  (testing "illegal number arguments"
    (is (thrown? Exception (add-operation-day-statement 2017.2 2017))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (add-operation-day-statement "ABCD" "ABCD"))))
  (testing "illegal arguments - statements-map must be a map"
    (is (thrown? Exception (add-operation-day-statement [] {}))))
  (testing "illegal arguments - statements-map must be a map"
    (is (thrown? Exception (add-operation-day-statement 12 {}))))
  (testing "illegal arguments - operation must be a map"
    (is (thrown? Exception (add-operation-day-statement {} []))))
  (testing "illegal arguments - operation must be a map"
    (is (thrown? Exception (add-operation-day-statement {} 12))))
  (testing "output - day operations must have a map with the new description value"
    (is (= (let [op (wrap-operation "123456" "description" 2000 "2017-02-03" "D")
                 ds (create-day-statement
                      (atom {"123456" (wrap-account "acc" "email@email.com")})
                      {}
                      op)]
             ((nth (get-in (add-operation-day-statement ds op) ["2017-02-03" :operations]) 0) :description))
           "description")))
  (testing "output - day operations must have a map with the new amount value"
    (is (= (let [op (wrap-operation "123456" "description" 2000 "2017-02-03" "D")
                 ds (create-day-statement
                      (atom {"123456" (wrap-account "acc" "email@email.com")})
                      {}
                      op)]
             ((nth (get-in (add-operation-day-statement ds op) ["2017-02-03" :operations]) 0) :amount))
           2000)))
  (testing "output - day operations must have a map with the new type value"
    (is (= (let [op (wrap-operation "123456" "description" 2000 "2017-02-03" "D")
                 ds (create-day-statement
                      (atom {"123456" (wrap-account "acc" "email@email.com")})
                      {}
                      op)]
             ((nth (get-in (add-operation-day-statement ds op) ["2017-02-03" :operations]) 0) :type))
           "D"))))

(deftest get-account-statement-test
  (testing "illegal nil arguments"
    (is (false? ((get-account-statement nil nil nil nil) :status))))
  (testing "illegal date string arguments"
    (is (false? ((get-account-statement "2017-02-03" "2017-03-03" "2017-03-03" "2017-03-03") :status))))
  (testing "illegal number arguments"
    (is (false? ((get-account-statement 2017.2 2017 2000 2000) :status))))
  (testing "illegal regular string parameters"
    (is (false? ((get-account-statement "ABCD" "ABCD" "DCBA" "DCBA") :status))))
  (testing "illegal arguments - accounts-map must be a map atom"
    (is (false? ((get-account-statement "ABCD" "123445" "2017-03-04" "2017-03-10") :status))))
  (testing "illegal arguments - accounts-map must be a map atom"
    (is (false? ((get-account-statement {} "123445" "2017-03-04" "2017-03-10") :status))))
  (testing "illegal arguments - account-number must be an integer"
    (is (false? ((get-account-statement (atom {"ABCD" {}}) "ABCD" "2017-03-04" "2017-03-10") :status))))
  (testing "illegal arguments - begin-date must be a date-string"
    (is (false? ((get-account-statement
                   (atom {"123456" (wrap-account "a" "a@a.com")}) "123456" "11000-50-04" "2017-03-10") :status))))
  (testing "illegal arguments - end-date must be a date-string"
    (is (false? ((get-account-statement
                   (atom {"123456" (wrap-account "a" "a@a.com")}) "123456" "2017-03-04" (format-date "2017-03-10")) :status))))
  (testing "output - day statement must have the correct number of operations"
    (is (= (let [acc-map (generate-dummy-accounts-map "123456" 100 10 "D")
                 st-map (get-account-statement acc-map "123456" dummy-date (date-string (get-today-date)))]
             (count ((get-in st-map [:statement dummy-date]) :operations))) 100)))
  (testing "output - day statement must have the correct balance"
    (is (= (let [acc-map (generate-dummy-accounts-map "123456" 100 10 "D")
                 st-map (get-account-statement acc-map "123456" dummy-date (date-string (get-today-date)))]
             ((get-in st-map [:statement dummy-date]) :balance)) -1000.0))))

(deftest get-debt-periods-test
  (testing "illegal nil arguments"
    (is (false? ((get-account-statement nil nil nil nil) :status))))
  (testing "illegal date string arguments"
    (is (false? ((get-account-statement "2017-02-03" "2017-03-03" "2017-03-03" "2017-03-03") :status))))
  (testing "illegal number arguments"
    (is (false? ((get-account-statement 2017.2 2017 2000 2000) :status))))
  (testing "illegal regular string parameters"
    (is (false? ((get-account-statement "ABCD" "ABCD" "DCBA" "DCBA") :status))))
  (testing "illegal arguments - accounts-map must be a map atom"
    (is (false? ((get-account-statement "ABCD" "123445" "2017-03-04" "2017-03-10") :status))))
  (testing "illegal arguments - accounts-map must be a map atom"
    (is (false? ((get-account-statement {} "123445" "2017-03-04" "2017-03-10") :status))))
  (testing "illegal arguments - account-number must be an integer"
    (is (false? ((get-account-statement (atom {"ABCD" {}}) "ABCD" "2017-03-04" "2017-03-10") :status))))
  (testing "illegal arguments - begin-date must be a date-string"
    (is (false? ((get-account-statement
                   (atom {"123456" (wrap-account "a" "a@a.com")}) "123456" "11000-50-04" "2017-03-10") :status))))
  (testing "illegal arguments - end-date must be a date-string"
    (is (false? ((get-account-statement
                   (atom {"123456" (wrap-account "a" "a@a.com")}) "123456" "2017-03-04" (format-date "2017-03-10")) :status))))
  (testing "two operations - one debit one credit - correct interval"
    (is (nil? ((nth ((get-debt-periods 
                       (insert-operation (wrap-operation "123456" "a" 20 "2017-01-01" "D") 
                                         (atom {"123456" (wrap-account "a" "a@b.c")}) "123456")
                       "123456"
                       "2016-02-02"
                       "2017-01-02") :debt-periods) 0) :end))))
  (testing "only one credit operation -> no debt periods"
    (is (let [debt-period (nth ((get-debt-periods 
                                  (insert-operation (wrap-operation "123456" "a" 20 "2017-01-01" "D")
                                                    (insert-operation (wrap-operation "123456" "a" 20 "2017-01-05" "C") 
                                                                      (atom {"123456" (wrap-account "a" "a@b.c")}) "123456")
                                                    "123456")
                                  "123456"
                                  "2016-02-02"
                                  "2017-01-06") :debt-periods) 0)]
          (and (= (debt-period :start) "2017-01-01") (= (debt-period :end) "2017-01-04"))))))
