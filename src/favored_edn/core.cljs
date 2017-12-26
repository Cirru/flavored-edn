
(ns favored-edn.core (:require [clojure.string :as string]))

(declare write-edn)

(declare write-map)

(declare write-set)

(declare write-vector)

(defn break-line [x] (string/join (str "\n" (string/join "" (repeat x " ")))))

(defn inc2 [x] (+ x 1))

(defn literal? [x]
  (or (number? x) (string? x) (keyword? x) (symbol? x) (nil? x) (boolean? x)))

(defn write-vector [xs indent]
  (if (or (empty? xs) (every? literal? xs))
    (pr-str xs)
    (str
     "["
     (break-line (inc2 indent))
     (let [new-indent (inc2 indent)]
       (->> xs (map (fn [x] (write-edn x new-indent))) (string/join (break-line new-indent))))
     (break-line indent)
     "]")))

(defn write-set [xs indent]
  (if (or (empty? xs) (every? literal? xs))
    (pr-str xs)
    (str
     "#{"
     (break-line (inc2 indent))
     (let [new-indent (inc2 indent)]
       (->> xs (map (fn [x] (write-edn x new-indent))) (string/join (break-line new-indent))))
     (break-line indent)
     "}")))

(defn write-map [dict indent]
  (if (or (empty? dict) (every? literal? (vals dict)))
    (pr-str dict)
    (if (every? string? (keys dict))
      (let [next-indent (inc2 indent)]
        (str
         "{"
         (break-line next-indent)
         (->> dict
              (sort-by first)
              (map (fn [[k v]] (str (pr-str k) " " (write-edn v next-indent))))
              (string/join (break-line next-indent)))
         (break-line indent)
         "}"))
      (let [simple-fields (->> dict (filter (fn [[k v]] (literal? v))))
            complicated-fields (->> dict (filter (fn [[k v]] (not (literal? v)))))
            next-indent (inc2 indent)]
        (str
         "{"
         (if (not (empty? simple-fields)) (break-line next-indent))
         (->> simple-fields
              (map (fn [[k v]] (str (pr-str k) " " (pr-str v))))
              (string/join ", "))
         (break-line next-indent)
         (->> complicated-fields
              (map (fn [[k v]] (str (pr-str k) " " (write-edn v next-indent))))
              (string/join (break-line next-indent)))
         (break-line indent)
         "}")))))

(defn write-edn
  ([data] (write-edn data 0))
  ([data indent]
   (cond
     (map? data) (write-map data indent)
     (vector? data) (write-vector data indent)
     (set? data) (write-set data indent)
     (number? data) (pr-str data)
     (string? data) (pr-str data)
     (boolean? data) (pr-str data)
     :else (pr-str data))))
