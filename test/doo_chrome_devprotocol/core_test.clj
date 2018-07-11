(ns doo-chrome-devprotocol.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [doo-chrome-devprotocol.core :as dc]))

(deftest runs-doo-in-chrome-test
  (testing "works with passing tests"
    (let [{:keys [success? report]} (dc/run "target/passing-tests.js")]
      (is (true? success?))
      (is (= {:tests 2
              :assertions 2
              :failures 0
              :errors 0}
             report))))

  (testing "works with failing tests and errors"
    (let [{:keys [success? report]} (dc/run "target/failing-tests.js")]
      (is (false? success?))
      (is (= {:tests 4
              :assertions 4
              :failures 2
              :errors 2}
             report))))

  (testing "detects timeout"
    (let [{:keys [success? report]} (dc/run "target/passing-tests.js" {:doo-run-timeout 0})]
      (is (false? success?))
      (is (= ::dc/timeout report)))))
