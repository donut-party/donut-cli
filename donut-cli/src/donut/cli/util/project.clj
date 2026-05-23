(ns donut.cli.util.project
  "utils for working with a project as a project" 
  (:require
   [babashka.fs :as fs]
   [babashka.process :as ps]
   [clojure.edn :as edn]
   [clojure.string :as str]))

(def env-path
  "/config/env.edn")

(defn project-root-git
  []
  (->> "git rev-parse --show-toplevel"
       (ps/process {:out :string})
       :out
       deref
       str/trim))

(defn project-root-config
  []
  (loop [cwd (fs/cwd)]
    (cond
      (nil? cwd)                          nil
      (fs/exists? (fs/path cwd env-path)) (str cwd)
      :else                               (recur (.getParent cwd)))))

(defn project-root
  []
  (if-let [project-root (or (project-root-git)
                            (project-root-config))]
    project-root
    (throw (ex-info "Could not find the donut project root from this directory" {}))))

(defn env-config-path
  []
  (fs/path (project-root) env-path))

(defn project-name
  []
  (-> (env-config-path)
      str
      slurp
      edn/read-string
      :project-name))

