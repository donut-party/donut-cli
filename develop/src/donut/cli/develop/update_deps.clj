(ns donut.cli.develop.update-deps
  "update donut-cli bb.edn with most recent deps"
  (:require
   [babashka.fs :as fs]
   [babashka.process :as ps]
   [clojure.string :as str]
   [donut.cli.develop.util :as util]
   [rewrite-clj.zip :as z]))

(defn update-git-sha [zloc dep-sym new-sha]
  (-> zloc
      (z/find-value z/next dep-sym)   ; find the dep key (e.g. 'party.donut/single-page-app)
      (z/find-value z/next :git/sha)  ; find :git/sha within that dep's map
      z/right                          ; move to the value (the sha string)
      (z/replace new-sha)))            ; replace with new sha

(defn -main
  []
  (let [proot       (util/project-root)
        latest-sha  (str/trim (:out (ps/sh "gh api repos/donut-party/single-page-app-template/branches/main --jq '.commit.sha'")))
        deps-edn-path (str (fs/path proot "donut-cli/deps.edn"))]
    (spit deps-edn-path
          (-> deps-edn-path
              slurp
              z/of-string
              (update-git-sha 'party.donut/single-page-app latest-sha)
              z/root-string))
    (try (ps/shell {:dir (fs/path proot "donut-cli")} "neil dep upgrade :lib \"party.donut/generate\"")
         (catch Exception e))
    (try (ps/shell {:dir (fs/path proot "donut-cli")} "neil dep upgrade :lib \"party.donut/generators\"")
         (catch Exception e))))
