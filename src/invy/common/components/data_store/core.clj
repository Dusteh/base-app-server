(ns invy.common.components.data-store.core
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log :refer :all]
            [taoensso.carmine :as car :refer [wcar]]
            [invy.common.components.data-store.data-impl :as data-impl]))

(defrecord Data-Store [config]
    component/Lifecycle
    (start [sys]
      (let [config (-> sys :config)
            cache-spec {:pool (car/connection-pool {}) 
                        :spec {:uri (get config :cache-provider-uri "redis://localhost:6379")}}
            db-spec {:dbtype "postgresql"
                     :dbname (get config :db-name, "invy")
                     :host (get config :db-host "localhost")
                     :port (get config :db-port 5432)
                     :user (get config :db-user, "postgres")
                     :password (get config :db-password, "invy")}
            provider {:cache-spec cache-spec
                      :db-impl (data-impl/load-table-defenitions (get config :tables) db-spec)
                      :cache-impl {}
                      :db-spec db-spec}]
        (assoc sys :provider provider)))
    (stop [sys]
      (dissoc sys :provider)))

(defn load-component [config]
  (map->Data-Store config))

  
