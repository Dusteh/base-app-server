(ns invy.common.components.http-service.uris)

(def uris (atom {"/" {:get (fn [req] {:table :xyx})}
                 "/api" {:get (fn [req] {:body "API"})}
                 "/telemetry" {:get (fn [req] {:body "Teleme-try"})}}))

;; I load the URIs for the HTTP service.
(defn load-uris [current-map]
  (merge current-map @uris))
