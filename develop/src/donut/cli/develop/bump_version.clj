(ns donut.cli.develop.bump-version
  (:require
   [babashka.fs :as fs]
   [clojure.string :as str]
   [donut.cli.develop.util :as util]))

(defn -main
  []
  (let [proot        (util/project-root)
        version-path (-> proot
                         (fs/path "donut-cli/VERSION")
                         str)
        version      (-> version-path
                         slurp
                         str/trim
                         (str/split #"\.")
                         vec)]
    (spit version-path (str/join #"." (update version 2 #(inc (parse-long %)))))))
