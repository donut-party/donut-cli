(ns donut.cli.new)

(defn- set-classpath
  "Required by deps.new"
  []
  (System/setProperty
   "java.class.path"
   ((requiring-resolve 'babashka.classpath/get-classpath))))

(def cli-spec
  {:target-dir {:alias :t}
   :override   {:alias :o}})

(def args->opts [:name])

(defn run-deps-new
  [{:keys [opts]}]
  (set-classpath)
  ((requiring-resolve 'org.corfield.new/create)
   (merge opts
          {:template 'party.donut/single-page-app})))
