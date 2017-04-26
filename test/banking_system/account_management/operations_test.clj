(ns banking-system.account-management.operations-test
  (:require
    [clojure.test :refer :all]
    [banking-system.helpers.fn :refer [date-equals? format-date]]
    [banking-system.account-management.operations :refer :all]
    [banking-system.account-management.accounts :refer :all]))

(deftest wrap-operation-test
  (testing "account contains correct account-number"
    (is (= ((wrap-operation "123456" "description" 2000 "2016-04-04" "D") :account-number) "123456")))
  (testing "account contains correct description"
    (is (= ((wrap-operation "123456" "description" 2000 "2016-04-04" "D") :description) "description")))
  (testing "account contains correct amount"
    (is (= ((wrap-operation "123456" "description" 2000 "2016-04-04" "D") :amount) 2000)))
  (testing "account contains correct date"
    (is (date-equals? ((wrap-operation "123456" "description" 2000 "2016-04-04" "D") :date) (format-date "2016-04-04"))))
  (testing "account contains correct type"
    (is (= ((wrap-operation "123456" "description" 2000 "2016-04-04" "D") :type) "D"))))

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
    (is (not (nil? (get-operations (atom {"123456" {:operations []}}) "123456"))))))

(deftest get-operation-amount-test
  (testing "illegal date string arguments"
    (is (thrown? Exception (get-operation-amount "2017-02-03"))))
  (testing "illegal number arguments"
    (is (thrown? Exception (get-operation-amount 2017.2))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (get-operation-amount "ABCD"))))
  (testing "illegal arguments - operation must be a map"
    (is (thrown? Exception (get-operation-amount [:type :amount]))))
  (testing "illegal arguments - operation must be a map"
    (is (thrown? Exception (get-operation-amount #{:type :amount}))))
  (testing "illegal arguments - operation must not be a atom"
    (is (thrown? Exception (get-operation-amount (atom {})))))
  (testing "returns correct amount debit"
    (is (= (get-operation-amount {:type "D" :amount 2000}) -2000)))
  (testing "returns correct amount credit"
    (is (= (get-operation-amount {:type "C" :amount 2000}) 2000))))

(deftest insert-operation-test
  (testing "illegal date string arguments"
    (is (thrown? Exception (insert-operation "2017-03-03" "2017-03-03" "2017-03-03"))))
  (testing "illegal number arguments"
    (is (thrown? Exception (insert-operation 2017.2 2017 2017.2))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (insert-operation "ABCD" "ABCD" "ABCD"))))
  (testing "illegal arguments - operation should be a map"
    (is (thrown? Exception (insert-operation "123455" (atom {}) 134456))))
  (testing "illegal arguments - account-number should be a integer string"
    (is (thrown? Exception (insert-operation (wrap-operation "123456" "a" 20 "2017-03-04" "D") (atom {}) "1234aaa"))))
  (testing "illegal arguments - accounts-map should be an atom"
    (is (thrown? Exception (insert-operation (wrap-operation "123456" "description" 20 "2017-03-04" "D") {} "123456"))))
  (testing "operation is now inside the map atom"
    (is (false? (empty? @((@(insert-operation (wrap-operation "123456" "description" 20 "2017-03-04" "D")
                                              (atom {"123456" (wrap-account "test" "test@test.com")})
                                              "123456") "123456") :operations))))))

(deftest create-operation-test
  (testing "illegal nil arguments"
    (is (false? ((create-operation nil nil nil nil nil nil) :status))))
  (testing "illegal date string arguments"
    (is (false? ((create-operation "2017-02-03" "2017-03-03" "2017-03-03" "2017-02-03" "2017-03-03" "2017-03-03") :status))))
  (testing "illegal number arguments"
    (is (false? ((create-operation 2017.2 2017 2000 2017.2 2017 2000) :status))))
  (testing "illegal regular string parameters"
    (is (false? ((create-operation "ABCD" "ABCD" "DCBA" "ABCD" "ABCD" "DCBA") :status))))
  (testing "illegal arguments - account-number should be an integer string"
    (is (false? ((create-operation (atom {}) "1234a" "test" 200 "2017-02-03" "D") :status))))
  (testing "illegal arguments - description should be a string"
    (is (false? ((create-operation (atom {}) "123456" 12.2 2000 "2016-03-04" "D") :status))))
  (testing "illegal arguments - amount should be a float"
    (is (false? ((create-operation (atom {}) "123456" "test" "test" "2017-02-03" "D") :status))))
  (testing "illegal arguments - date should be a string"
    (is (false? ((create-operation (atom {}) "123456" "test" 2000 2 "D") :status))))
  (testing "illegal arguments - type should be a string"
    (is (false? ((create-operation (atom {}) "123456" "test" 2000 "2017-02-03" 22) :status))))
  (testing "illegal arguments - atom must have account number"
    (is (false? ((create-operation (atom {"234506" (wrap-account "test" "test@test.com")}) "123456" "test" 2000 "2017-02-03" "D") :status))))
  (testing "operation is created"
    (is (true? ((create-operation (atom {"123456" (wrap-account "test" "test@test.com")}) "123456" "test" 3000 "2017-03-03" "D") :status)))))