(ns user
  (:require [invy.service.core :as invy]))


(defn start [args]
  (invy/run args)
  (in-ns `invy.service.core))


