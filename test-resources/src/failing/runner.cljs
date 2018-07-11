(ns failing.runner
  (:require [doo.runner :refer-macros [doo-all-tests]]
            [failing.tests]))

(doo-all-tests #".*tests$")
