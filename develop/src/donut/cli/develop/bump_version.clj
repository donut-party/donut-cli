(ns donut.cli.develop.bump-version
  (:require
   [babashka.fs :as fs]
   [clojure.string :as str]
   [donut.cli.develop.util :as util]))

(defn version-path
  []
  (-> (util/project-root)
      (fs/path "donut-cli/resources/VERSION")
      str))

(defn version
  []
  (-> (version-path)
      slurp
      str/trim))

(defn version-parts
  []
  (-> (version)
      (str/split #"\.") 
      vec))

(defn -main
  []
  (spit (version-path)
        (str/join #"."
                  (update (version-parts) 2 #(inc (parse-long %))))))
