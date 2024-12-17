(ns invy.common.components.telemetry.logger
  (:import [io.opentelemetry.instrumentation.log4j.appender.v2_17 OpenTelemetryAppender]
           [io.opentelemetry.sdk OpenTelemetrySdk])
  (:require [clojure.tools.logging :as log :refer :all]
            [invy.utils.strings :as s]))

(defn configure-otel-logging [logger-config]
  (let [otel-instance (:otel-instance logger-config)]
    (try 
      true
      (catch Exception ex
        (debug (s/g :common.components.telemetry.logger.fail.otel-logger.config) ex)
        false))))


(defn configure [logger-config]
  "For any specefic logging configuration we need to load"
  (let [log-instance {:configured "Automatically"
                      :otel-loging? (configure-otel-logging logger-config)}]
    log-instance))
