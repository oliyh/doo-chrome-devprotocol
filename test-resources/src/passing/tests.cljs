(ns passing.tests
  (:require [cljs.test :refer-macros [deftest is async]]))

(deftest passes-sync
  (js/console.log "Running sync test")
  (is (= 1 1)))

(deftest passes-async
  (js/console.log "Running async test")
  (async done
         (js/setTimeout (fn []
                          (is (= 1 1))
                          (js/console.log "Completing async test")
                          (done)) 2000)))
