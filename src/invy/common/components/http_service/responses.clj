(ns invy.common.components.http-service.responses
  (:require [ring.util.response :as response]
            [clojure.tools.logging :as log :refer :all]
            [steffan-westcott.clj-otel.api.trace.span :as span]
            [invy.utils.strings :as s]
            [clojure.data.json :as j]
            [invy.utils.constants :as c]))

;;takes a response and wraps it in the expected
;;ring response type map as a json, hopefully something makes
;;it back to the caller
(defn data-response 
  [response]
  (let [response (if (= (type response) (type {})) response {:data response})
        response (merge {:body (j/write-str response)
                         :headers {"content-type" (get response :content-type "application/json")}} ;; DUSTY DO NOT SUPPLY THIS, JUST RETURN MAPS
                        (if response response {}))]
    (trace ::data-response "\n" (s/pfmt response))
    response))

(defn error-response 
  ([{:keys [error-message-key error-req error-uuid error-code] :as response}]
   (let [err-response {:body (j/write-str {:error-message (s/g error-message-key)
                                           ;:core-response (s/pfmt response)
                                           :error-message-key error-message-key
                                           :error-req (s/pfmt error-req)
                                           :error-uuid error-uuid})
                       :status error-code
                       :headers {"content-type" "application/json"}}]
     ;;Housekeeping here where it's convinent 
     (span/add-span-data! {:attributes {:error-data (-> err-response :body)}})
     (trace ::error-response "\n" (s/pfmt error-response))
     err-response))
  ([] (error-response {})))

(defn wrap-response [response]
  (data-response response))
