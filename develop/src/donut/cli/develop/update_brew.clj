(ns donut.cli.develop.update-brew
  "update the homebrew formula"
  (:require
   [babashka.fs :as fs]
   [babashka.process :as ps]
   [clojure.string :as str]
   [donut.cli.develop.bump-version :as bump-version]
   [donut.cli.develop.util :as util]
   [selmer.parser :as selmer]))

(def release-pattern
  "https://github.com/donut-party/donut-cli/archive/refs/tags/v%s.zip")

(def formula-path
  (fs/path (util/project-root) "../homebrew-brew/Formula"))

(defn -main
  []
  (let [version     (bump-version/version)
        release-url (format release-pattern version)
        dir         (str (fs/create-temp-dir))
        _           (ps/shell {:dir dir} "wget" release-url "-q" "-O" "release.zip")
        sha256      (first (str/split (:out (ps/sh {:dir dir} "sha256sum release.zip"))
                                      #"\s+"))
        template    (slurp (str (fs/path formula-path "donut.rb.template")))]
    (spit (str (fs/path formula-path "donut.rb"))
          (selmer/render template {:version version :sha256 sha256}))
    (println "still need to commit and push change in homebrew-brew repo")))
