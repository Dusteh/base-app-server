(ns invy.service.core
  (:require [clojure.main :as main]
            [clojure.tools.logging :as log :refer [debug info trace]]
            [invy.utils.core :as utils]
            [invy.utils.strings :as s]
            [invy.common.components.telemetry.core :as telemetry]
            [invy.common.components.http-service.core :as http-service]
            [invy.common.components.data-store.core :as data]
            [com.stuartsierra.component :as component]))


(defn system-map [processed-args]
  (component/system-map
    :configuration (component/using (utils/load-component processed-args) {:telemetry :telemetry})
    :http-service (component/using (http-service/load-component processed-args)
                                   {:telemetry :telemetry})
    :data-store (component/using (data/load-component processed-args)
                                 {:http-service :http-service
                                  :telemetry :telemetry}) 
    :telemetry (telemetry/load-component processed-args)))

(defonce running-system (atom {}))

(defn start-service [processed-args]
  (let [sys-config (system-map processed-args)
        sys (component/start sys-config)]
    (trace ::start-service (s/g :service.core.debug.running.system.configuration) "\n\n" (-> sys s/pfmt))
    (reset! running-system sys)
    sys))

(defn stop-service []
  (component/stop @running-system)
  (reset! running-system {}
  (info ::stop-service (s/g :service.core.info.stop))))


(defn run [args]
  (info ::run (s/g :service.core.info.starting.system))
  (debug ::run (s/g :service.core.debug.starting.system)  (str args))
  (start-service (utils/process-args args)))

(defn -main []
  (run {}))

