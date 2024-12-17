(ns invy.common.components.http-service.core
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log :refer :all]
            [ring.middleware.params :as params]
            [ring.util.response :as response] 
            [ring.adapter.jetty :as jetty]
            [steffan-westcott.clj-otel.api.trace.http :as trace-http]
            [invy.common.components.http-service.responses :refer :all]
            [invy.common.components.http-service.middleware :refer :all]
            [invy.common.components.http-service.uris :as uris]
            [invy.utils.strings :as s]))

(defn handler-wrapper [response-handler runtime-uri-mapper]
  (fn handler [{:keys [request-method uri] :as req}]
    (response-handler
      (if-let [uri-fn (-> (get runtime-uri-mapper uri) request-method)]
            (data-response (uri-fn req))
            (let [error-uuid (random-uuid)]
              (error (str error-uuid "\n" (s/g :common.components.http-service.handler.not-found) "\n" (s/pfmt req))) 
              (error-response {:error-message-key :common.componentsq.http-service.handler.not-found
                               :error-req req
                               :error-uuid error-uuid
                               :error-code 404}))))))

(defn response-handler [res]
  (info ::response-handler-updated)
  ;;I'm an interceptor
  res)

(defn build-server [config sys]
  (let [runtime-uri-mapper (uris/load-uris {})
        router-map (-> response-handler
                       (handler-wrapper runtime-uri-mapper);;Order matters here!
                       (wrap-service sys)
                       (wrap-logging)
                       (wrap-exception)
                       (wrap-telemetry)
                       trace-http/wrap-server-span)
        jetty-instance (jetty/run-jetty router-map
                                        {:port (get config :http-service.port 8080)
                                         :join? false})]
    jetty-instance))
 
(defn stop-server [sys]
  (let [jetty-instance (-> sys :provider :server)]
    (info (.stop jetty-instance))))

(defrecord Http-Service [config]
  component/Lifecycle
  (start [{:keys [config] :as sys}]
    (assoc sys :provider {:server (build-server config sys)}))
  (stop [sys]
    (stop-server sys)
    (dissoc sys :provider)))

(defn load-component [config]
  (map->Http-Service config))


