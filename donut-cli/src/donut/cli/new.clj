(ns donut.cli.new 
  (:require
   [babashka.cli :as cli]))

(defn- set-classpath
  "Required by deps.new"
  []
  (System/setProperty
   "java.class.path"
   ((requiring-resolve 'babashka.classpath/get-classpath))))

(def cli-opts
  {:target-dir {:alias :t
                :desc  "directory to create the project in"}
   :overwrite  {:alias :o
                :desc  "overwrite directory if it exists"}
   :name       {:alias :n
                :desc  "project name in group-name/artifact-name format"}})

(def cli-spec
  {:spec       cli-opts
   :args->opts [:name]})

(defn run-deps-new
  [opts]
  (set-classpath)
  ((requiring-resolve 'org.corfield.new/create)
   (merge opts
          {:template 'party.donut/single-page-app})))

(defn print-help
  [spec]
  (println "creates a new Donut single-page app")
  (println "example: donut new my-co/my-project")
  (println (cli/format-opts (merge spec {:order (vec (keys (:spec spec)))}))))

(defn -main
  [& args]
  (if args
    (let [opts (cli/parse-opts args cli-spec)]
      (if (or (:help opts) (:h opts))
        (print-help cli-spec)
        (run-deps-new opts)))
    (print-help cli-spec)))
