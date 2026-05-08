(ns donut.cli.version 
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]))

(defn -main
  []
  (println (str/trim (slurp (io/resource "VERSION")))))
