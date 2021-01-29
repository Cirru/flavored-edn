
(ns flavored-edn.updater (:require [respo.cursor :refer [update-states]]))

(defn updater [store op op-data]
  (case op
    :states (update-states store op-data)
    :content (assoc store :content op-data)
    :result (assoc store :result op-data)
    store))
