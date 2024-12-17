(ns invy.utils.strings
  (:require [com.stuartsierra.component :as component]
            [clojure.pprint :as pprint]))

(def string-map {
  :service.core.debug.running.system.configuration {:en "\n=====================\nRunning System Configuration\n====================="}
  :service.core.debug.starting.system {:en "Starting system with arguments:\n"} 
  :service.core.info.stop {:en "System Stopped"}
  :service.core.info.starting.system {:en "Starting system"}
  :common.components.http-service.build-server.start {:en "Starting RING server with config\n"}
  :common.components.http-service.wrap-exception {:en "Error processing\n"}
  :common.components.telemetry.logger.fail.otel-logger.config  {:en "Failed to configure logging for otel"}})



(defn g ([key-val] (g key-val, :en)) 
        ([key-val, lang]
         (or (-> string-map key-val lang)  
             key-val)))

(defn pfmt [strn]
  (with-out-str
    (pprint/pprint strn)))
