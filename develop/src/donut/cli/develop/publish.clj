(ns donut.cli.develop.publish 
  (:require
   [babashka.fs :as fs]
   [babashka.process :as ps]
   [donut.cli.develop.bump-version :as bump-version]
   [donut.cli.develop.update-deps :as update-deps]
   [donut.cli.develop.util :as util]))

(defn -main
  []
  (update-deps/-main)
  (bump-version/-main)
  (ps/shell "git add .")
  (let [proot       (util/project-root)
        version     (slurp (str (fs/path proot "donut-cli/VERSION")))
        tag-name    (str "v" version)]
    (ps/shell (format "git commit -m 'Bump version %s'" version))
    (ps/shell (str "git tag " tag-name))
    (ps/shell "git push --atomic origin main" tag-name)))
