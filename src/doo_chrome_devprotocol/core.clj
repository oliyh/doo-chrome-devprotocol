(ns doo-chrome-devprotocol.core
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log])
  (:import [java.io File]
           [java.nio.file Path]
           [java.util ArrayList List]
           [java.util.function Predicate]
           [io.webfolder.cdp Launcher]
           [io.webfolder.cdp.session SessionFactory Session]
           [io.webfolder.cdp.listener EventListener]
           [io.webfolder.cdp.event Events]
           [io.webfolder.cdp.event.runtime ConsoleAPICalled]
           [io.webfolder.cdp.type.runtime RemoteObject]))

(defn with-chrome-session [{:keys [port os chrome-path chrome-args chrome-launch-timeout] :as opts} f]
  (log/info "Launching Chrome")
  (let [launcher (Launcher. ^int port)
        chrome-path (or chrome-path (.findChrome launcher))
        ^SessionFactory session-factory (.launch launcher (.toPath (io/file chrome-path))
                                                 (ArrayList. ^List chrome-args))]
    (try
      (Thread/sleep chrome-launch-timeout)
      (let [^Session session (.create session-factory)]
        (try
          (.activate session)
          (f session opts)
          (catch Exception e
            (log/error e)
            (throw e))
          (finally
            (.close session))))
      (catch Exception e
        (log/error e)
        (throw e))
      (finally
        (log/info "Closing Chrome")
        (.kill launcher)))))

(defn- ^File create-test-page [{:keys [^File js-test-file] :as opts}]
  (let [test-page (-> js-test-file .getParent (io/file "doo-test.html"))]
    (spit test-page (-> (slurp (io/resource "doo-test.html"))
                        (string/replace "__js-test-file__" (.getAbsolutePath js-test-file))))
    test-page))

(defn- completion-event-listener [completion-promise]
  (let [test-report (atom {})]
    (reify EventListener
      (onEvent [this event value]
        (when (= Events/RuntimeConsoleAPICalled event)
          (when-let [message (.getValue ^RemoteObject (first (.getArgs ^ConsoleAPICalled value)))]
            (let [report (condp re-find message

                           #"(\d+) failures, (\d+) errors"
                           :>> (fn [[_ failure-count error-count]]
                                 (swap! test-report assoc
                                        :failures (Integer/parseInt failure-count)
                                        :errors (Integer/parseInt error-count)))

                           #"Ran (\d+) tests containing (\d+) assertions"
                           :>> (fn [[_ test-count assertion-count]]
                                 (swap! test-report assoc
                                        :tests (Integer/parseInt test-count)
                                        :assertions (Integer/parseInt assertion-count)))

                           @test-report)]

              (when (every? @test-report [:tests :assertions :failures :errors])
                (deliver completion-promise @test-report)))))))))

(defn- logging-event-listener [{:keys [doo-message-prefix]}]
  (reify EventListener
    (onEvent [this event value]
      (when (= Events/RuntimeConsoleAPICalled event)
        (let [message (.getValue ^RemoteObject (first (.getArgs ^ConsoleAPICalled value)))
              doo-message (some->> message (re-find (re-pattern (str "(?s)" doo-message-prefix "(.*)"))) second)]
          (if-not (string/blank? doo-message)
            (log/info doo-message)
            (log/debug message)))))))

(def doo-loaded?
  (reify Predicate
    (test [this session]
      (.evaluate ^Session session "window.doo == null"))))

(defn run-doo [^Session session {:keys [document-load-timeout
                                        doo-load-timeout
                                        doo-message-prefix
                                        doo-run-timeout] :as opts}]
  (let [test-page (create-test-page opts)
        completion-promise (promise)]
    (try
      (doto session
        (.enableConsoleLog)
        (.addEventListener (logging-event-listener opts))
        (.addEventListener (completion-event-listener completion-promise))
        (.navigate (format "file:///%s" (.getAbsolutePath test-page)))
        (.waitDocumentReady document-load-timeout)
        (.waitUntil doo-loaded? doo-load-timeout))

      (log/info "Running doo")
      (.evaluate session (-> (slurp (io/resource "doo-shim.js"))
                             (string/replace "__doo-message-prefix__" doo-message-prefix)))

      (let [report (deref completion-promise doo-run-timeout ::timeout)]
        {:success? (.evaluate session "success")
         :report report})

      (finally
        (io/delete-file test-page)))))

(def default-options
  {:port 8899
   ;; :chrome-path "/opt/google/chrome/chrome" ;; if needed to override
   :chrome-args ["--headless"
                 ;; "--disable-gpu" ;; recommended on windows
                 ]
   :chrome-launch-timeout 5000
   :doo-message-prefix "doo:"
   :document-load-timeout 60000
   :doo-load-timeout 10000
   :doo-run-timeout 60000})

(defn run [js-test-file & [opts]]
  (let [opts (assoc (merge default-options opts)
                    :js-test-file (io/file js-test-file))]
    (with-chrome-session opts
      run-doo)))
