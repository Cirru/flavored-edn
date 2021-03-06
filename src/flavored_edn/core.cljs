
(ns flavored-edn.core (:require [clojure.string :as string]))

(declare write-edn)

(declare write-list)

(declare write-map)

(declare write-set)

(declare write-vector)

(defn break-line [x] (str "\n" (string/join "" (repeat x " "))))

(defn literal? [x]
  (or (number? x) (string? x) (keyword? x) (symbol? x) (nil? x) (boolean? x)))

(defn write-vector [xs indent options]
  (if (or (empty? xs) (every? literal? xs))
    (pr-str xs)
    (let [new-indent (+ indent (:indent options))]
      (loop [acc "[", prev :space, body xs]
        (cond
          (empty? body) (str acc (if (= prev :block) " " "") "]")
          (literal? (first body))
            (recur
             (str
              acc
              (case prev
                :block (break-line new-indent)
                :literal " "
                :space ""
                (throw (js/Error. "Unknown state")))
              (pr-str (first body)))
             :literal
             (rest body))
          :else
            (recur
             (str acc (break-line new-indent) (write-edn (first body) new-indent options))
             :block
             (rest body)))))))

(defn write-set [xs indent options]
  (if (or (empty? xs) (every? literal? xs))
    (pr-str xs)
    (str
     "#{"
     (break-line (+ indent (:indent options)))
     (let [new-indent (+ indent (:indent options))]
       (->> xs
            (map (fn [x] (write-edn x new-indent options)))
            (string/join (break-line new-indent))))
     (break-line indent)
     "}")))

(defn write-map [dict indent options]
  (if (or (empty? dict) (every? literal? (vals dict)))
    (pr-str dict)
    (if (every? string? (keys dict))
      (let [next-indent (+ indent (:indent options))]
        (str
         "{"
         (break-line next-indent)
         (->> dict
              (sort-by first)
              (map (fn [[k v]] (str (pr-str k) " " (write-edn v next-indent options))))
              (string/join (break-line next-indent)))
         (break-line indent)
         "}"))
      (let [simple-fields (->> dict (filter (fn [[k v]] (literal? v))))
            complicated-fields (->> dict (filter (fn [[k v]] (not (literal? v)))))
            next-indent (+ indent (:indent options))]
        (str
         "{"
         (if (not (empty? simple-fields)) (break-line next-indent))
         (->> simple-fields
              (map (fn [[k v]] (str (pr-str k) " " (pr-str v))))
              (string/join ", "))
         (break-line next-indent)
         (->> complicated-fields
              (map (fn [[k v]] (str (pr-str k) " " (write-edn v next-indent options))))
              (string/join (break-line next-indent)))
         (break-line indent)
         "}")))))

(defn write-list [xs indent options]
  (let [new-indent (+ indent (:indent options))]
    (loop [acc "(", prev :space, body xs]
      (cond
        (empty? body) (str acc (if (= prev :block) " " "") ")")
        (literal? (first body))
          (recur
           (str
            acc
            (case prev
              :block (break-line new-indent)
              :literal " "
              :space ""
              (throw (js/Error. "Unknown state")))
            (pr-str (first body)))
           :literal
           (rest body))
        :else
          (recur
           (str acc (break-line new-indent) (write-edn (first body) new-indent options))
           :block
           (rest body))))))

(defn write-edn
  ([data] (write-edn data 0 {:indent 1}))
  ([data options] (write-edn data 0 options))
  ([data indent options]
   (cond
     (map? data) (write-map data indent options)
     (vector? data) (write-vector data indent options)
     (set? data) (write-set data indent options)
     (seq? data) (write-list data indent options)
     (number? data) (pr-str data)
     (string? data) (pr-str data)
     (boolean? data) (pr-str data)
     :else (pr-str data))))
