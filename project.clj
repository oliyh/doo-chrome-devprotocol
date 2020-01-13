(defproject doo-chrome-devprotocol "0.1.1-SNAPSHOT"
  :description "A doo runner for Chrome using the Chrome Dev Protocol on the JVM"
  :url "https://github.com/oliyh/doo-chrome-devprotocol"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [io.webfolder/cdp4j "3.0.2"]
                 [org.clojure/tools.logging "0.4.1"]]
  :java-source-paths ["src"]
  :javac-options ["-target" "1.8" "-source" "1.8"]
  :plugins [[lein-doo "0.1.10"]
            [lein-cljsbuild "1.1.7"]]
  :profiles {:dev {:resource-paths ["test-resources"]
                   :dependencies [[org.clojure/clojurescript "1.10.339"]
                                  [lein-doo "0.1.10"]]}}

  :cljsbuild {:builds [{:id "passing-tests"
                        :source-paths ["test-resources/src"]
                        :compiler {:output-to "target/passing-tests.js"
                                   :main passing.runner
                                   :optimizations :simple}}

                       {:id "failing-tests"
                        :source-paths ["test-resources/src"]
                        :compiler {:output-to "target/failing-tests.js"
                                   :main failing.runner
                                   :optimizations :simple}}]}

  :aliases {"test" ["do"
                    ["clean"]
                    ["cljsbuild" "once" "passing-tests" "failing-tests"]
                    ["test"]]})
