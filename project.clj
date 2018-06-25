(defproject doo-chrome-devprotocol "0.1.0-SNAPSHOT"
  :description "A doo runner for Chrome using the Chrome Dev Protocol on the JVM"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [io.webfolder/cdp4j "3.0.1"]
                 [org.clojure/tools.logging "0.4.1"]]
  :java-source-paths ["src"]
  :javac-options ["-target" "1.8" "-source" "1.8"]
  :profiles {:dev {:resource-paths ["test-resources"]}})
