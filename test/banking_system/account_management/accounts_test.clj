(ns banking-system.account-management.accounts-test
  (:require
    [clojure.test :refer :all]
    [banking-system.account-management.accounts :refer :all]))

(deftest wrap-account-test
  (testing "account contains correct name"
    (is (= ((wrap-account "testname" "testemail@email.com") :name) "testname")))
  (testing "account contains correct email"
    (is (= ((wrap-account "testname" "testemail@email.com") :email) "testemail@email.com")))
  (testing "account contains an operation atom"
    (is (= (type ((wrap-account "testname" "testemail@email.com") :operations)) clojure.lang.Atom))))

(deftest generate-account-number-test
  (testing "illegal date string arguments"
    (is (thrown? Exception (generate-account-number "2017-02-03"))))
  (testing "illegal number arguments"
    (is (thrown? Exception (generate-account-number 2017.2))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (generate-account-number "ABCD"))))
  (testing "illegal collection argument - not an atom"
    (is (thrown? Exception (generate-account-number {}))))
  (testing "illegal collection argument - not an atom"
    (is (thrown? Exception (generate-account-number []))))
  (testing "illegal collection argument - not an atom"
    (is (thrown? Exception (generate-account-number #{}))))
  (testing "account number is not used by another user"
    (is (false? (let [accs (atom {123421 {} 122345 {} 124234 {} 331204 {}})]
                  (contains? @accs (generate-account-number accs)))))))

(deftest remove-account-test
  (testing "illegal date string arguments"
    (is (thrown? Exception (remove-account "2017-03-03" "2017-03-03"))))
  (testing "illegal number arguments"
    (is (thrown? Exception (remove-account 2017.2 2017))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (remove-account "ABCD" "ABCD"))))
  (testing "illegal combination of number/map atom"
    (is (thrown? Exception (remove-account (atom {}) 123456))))
  (testing "illegal arguments - account number should be a integer string"
    (is (thrown? Exception (remove-account "2017-03-03" (atom {})))))
  (testing "illegal arguments - accounts map should be an atom map"
    (is (thrown? Exception (remove-account "20170303" (atom [])))))
  (testing "account number is not in the atom anymore"
    (is (false? (contains? @(remove-account "123456" (atom {"123456" {}})) "123456")))))

(deftest insert-account-test
  (testing "illegal date string arguments"
    (is (thrown? Exception (insert-account "2017-03-03" "2017-03-03" "2017-03-03" "2017-03-03"))))
  (testing "illegal number arguments"
    (is (thrown? Exception (insert-account 2017.2 2017 2017.2 2017))))
  (testing "illegal regular string parameters"
    (is (thrown? Exception (insert-account "ABCD" "ABCD" "ABCD" "ABCD"))))
  (testing "illegal arguments - username should be a string"
    (is (thrown? Exception (insert-account "123455" (atom {}) 134456 "test"))))
  (testing "illegal arguments - email should be a email string"
    (is (thrown? Exception (insert-account "123455" (atom {}) "testname" 12.2))))
  (testing "illegal arguments - account number should be a integer string"
    (is (thrown? Exception (insert-account "2017-03-03" (atom {}) "test" "testemail@b.c"))))
  (testing "account number is now inside the map atom"
    (is (true? (contains? @(insert-account "123456" (atom {}) "test" "test@test.com") "123456")))))

(deftest create-account-test
  (testing "illegal nil arguments"
    (is (false? ((create-account nil nil nil) :status))))
  (testing "illegal date string arguments"
    (is (false? ((create-account "2017-02-03" "2017-03-03" "2017-03-03") :status))))
  (testing "illegal number arguments"
    (is (false? ((create-account 2017.2 2017 2000) :status))))
  (testing "illegal regular string parameters"
    (is (false? ((create-account "ABCD" "ABCD" "DCBA") :status))))
  (testing "illegal arguments - username should be a string"
    (is (false? ((create-account (atom {}) 1413 "test@c.c") :status))))
  (testing "illegal arguments - email should be a string"
    (is (false? ((create-account (atom {}) "testname" 12.2) :status))))
  (testing "illegal arguments - accounts-map should be an atom"
    (is (false? ((create-account {} "testname" "testemail@c.b") :status))))
  (testing "account is created"
    (is (true? ((create-account (atom {}) "name" "email@email.com") :status)))))
