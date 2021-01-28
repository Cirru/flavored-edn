
(ns flavored-edn.compare
  (:require ["fs" :as fs]
            [fipp.edn :refer [pprint]]
            [flavored-edn.core :refer [write-edn]]
            [cljs.reader :refer [read-string]]))

(defn main! []
  (let [data (read-string (fs/readFileSync "coir.edn" "utf8")), now! #(.valueOf (js/Date.))]
    (let [start (now!)] (dotimes [x 20] (pr-str data)) (println (- (now!) start)))
    (let [start (now!)] (dotimes [x 20] (write-edn data)) (println (- (now!) start)))
    (let [start (now!)]
      (dotimes [x 1] (with-out-str (pprint data)))
      (println (- (now!) start)))))
