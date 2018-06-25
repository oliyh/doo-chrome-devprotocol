(ns doo-chrome-devprotocol.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [doo-chrome-devprotocol.core :as dc]))

(deftest runs-doo-in-chrome-test
  (is (true? (dc/run "test-resources/passing-tests.js")))
  (is (false? (dc/run "test-resources/failing-tests.js"))))
