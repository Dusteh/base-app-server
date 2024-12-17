(ns invy.utils.core
  [:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log :refer [debug info trace]]])


(defn process-args [args-map]
  args-map)

(defn process-resource [resource]
  [])

(defrecord Configuration 
  ;"Load application specefic configuration edns"
  [config]
  component/Lifecycle
  (start [this]
         (assoc this :tables (process-resource "tables.edn")))
  (stop [this]))


(defn load-component [config]
  (map->Configuration config))
