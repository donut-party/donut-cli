(ns donut.cli.new)

(defn- set-classpath
  "Required by deps.new"
  []
  (System/setProperty
   "java.class.path"
   ((requiring-resolve 'babashka.classpath/get-classpath))))

;; Support:
;; donut new my-app
;; donut new my-org/my-app
;; donut new my-app --target-dir=xyz
;; donut new foo --target-dir=xyz --overwrite

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


;; clojure \
;; -Sdeps '{:deps {party.donut/single-page-app {:local/root "./"}}}'\
;; -Tnew create \
;; :template party.donut/single-page-app \
;; :name donut-template-test/test-app \
;; :target-dir ../template-test
