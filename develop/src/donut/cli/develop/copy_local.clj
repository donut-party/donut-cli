(ns donut.cli.develop.copy-local
  (:require
   [babashka.fs :as fs]
   [babashka.process :as ps]
   [clojure.string :as str]
   [donut.cli.util.donut-project :as proj]))

(defn brew-path
  []
  (let [donut-dir-path (fs/path (str/trim (:out (ps/sh "brew --repository")))
                                "Cellar/donut")
        version        (str/trim (:out (ps/sh "ls" (str donut-dir-path))))]
    (fs/path donut-dir-path version "libexec/donut-cli")))

(defn -main
  []
  (let [source      (fs/path (proj/project-root) "donut-cli")
        destination (brew-path)]
    (fs/delete-tree destination)
    (fs/copy-tree source destination)))
