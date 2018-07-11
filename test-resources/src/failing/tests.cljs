(ns failing.tests
  (:require [cljs.test :refer-macros [deftest is async]]))

(deftest fails-sync
  (js/console.log "Running sync test")
  (is (= 1 2)))

(deftest fails-async
  (js/console.log "Running async test")
  (async done
         (js/setTimeout (fn []
                          (is (= 1 2))
                          (js/console.log "Completing async test")
                          (done)) 2000)))

(deftest errors-sync
  (js/console.log "Running sync error")
  (is (= :a (re-find 1 "hello"))))

(deftest errors-async
  (js/console.log "Running async error")
  (async done
         (js/setTimeout (fn []
                          (is (= :a (re-find 1 "hello")))
                          (js/console.log "Completing async error")
                          (done)) 2000)))
