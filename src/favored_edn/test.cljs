
(ns favored-edn.test
  (:require [cljs.test :refer [deftest run-tests is testing]]
            ["fs" :as fs]
            [cljs.reader :refer [read-string]]
            [favored-edn.core :refer [write-edn]]))

(defn slurp [x] (fs/readFileSync x "utf8"))

(deftest
 test-base-data
 (testing
  "test a set"
  (let [code (slurp "examples/set.edn")] (is (= code (write-edn (read-string code))))))
 (testing
  "test a vector"
  (let [code (slurp "examples/vector.edn")] (is (= code (write-edn (read-string code))))))
 (testing
  "test a list"
  (let [code (slurp "examples/list.edn")] (is (= code (write-edn (read-string code))))))
 (testing
  "test a mixed data"
  (let [code (slurp "examples/mixed.edn")] (is (= code (write-edn (read-string code))))))
 (testing
  "test a map"
  (let [code (slurp "examples/map.edn")] (is (= code (write-edn (read-string code)))))))

(deftest
 test-coir
 (testing
  "test a set"
  (let [code (slurp "examples/base-coir.edn")] (is (= code (write-edn (read-string code)))))))

(defn main! [] (run-tests))
