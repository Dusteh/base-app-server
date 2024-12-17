(ns invy.common.components.telemetry.core
  (:require [invy.common.components.telemetry.logger :as logger] 
            [com.stuartsierra.component :as component]))


(defrecord Telemetry [config]
  component/Lifecycle
  (start [{:keys [config] :as sys}]
    (assoc sys :provider {:logging (logger/configure (get config :logger-config {:no-config []}))}))
  (stop [sys]
    (dissoc sys :provider {})))


(defn load-component [config]
  (map->Telemetry config))
