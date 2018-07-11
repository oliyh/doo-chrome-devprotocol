(ns passing.runner
  (:require [doo.runner :refer-macros [doo-all-tests]]
            [passing.tests]))

(doo-all-tests #".*tests$")
