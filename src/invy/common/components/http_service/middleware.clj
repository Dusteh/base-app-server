(ns invy.common.components.http-service.middleware
  (:require [clojure.tools.logging :as log :refer :all]
            [invy.common.components.http-service.responses :refer :all]
            [steffan-westcott.clj-otel.api.trace.http :as trace-http]
            [steffan-westcott.clj-otel.api.trace.span :as span]
            [invy.utils.strings :as s]))


(defn wrap-exception [handler]
 (fn [req]
   (try 
     (handler req)
     (catch Exception ex
       (let [error-uuid (random-uuid)
             error-dat {:error-message-key :common.components.http-service.wrap-exception.error.message
                        :error-req req 
                        :error-uuid error-uuid
                        :error-code 500}];; Do not include the raw exception, it's insecure and exposes interal opperation of the service
         (error (str error-uuid "\n" (s/g :common.components.http-service.wrap-exception)) ex)
         (error-response error-dat))))))

(defn wrap-service [handler sys]
  (fn [request]
    (handler (assoc request :sys sys))))

(defn wrap-logging [handler]
  (fn [request]
    (trace ::wrap-logging.fn (s/pfmt request))
    (handler request)))

(defn wrap-telemetry [handler]
  (fn [request]
;;    (span/add-span-data! {:attributes {:count-of 1}})
    (handler request)))
