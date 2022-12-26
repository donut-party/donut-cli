(ns donut.cli.new
  (:require
   [org.corfield.new :as new]))

(defn- set-classpath
  "Required by deps.new"
  []
  (System/setProperty
   "java.class.path"
   ((requiring-resolve 'babashka.classpath/get-classpath))))

(defn run-deps-new
  [opts]
  ;; ((requiring-resolve 'babashka.deps/add-deps) {:deps '{party.donut/single-page-app {:local/root "../single-page-app"}}})
  (set-classpath)
  ((requiring-resolve 'org.corfield.new/create)
   {:template   'party.donut/single-page-app
    :name       "cli-new-test"}))


;; clojure \
;; -Sdeps '{:deps {party.donut/single-page-app {:local/root "./"}}}'\
;; -Tnew create \
;; :template party.donut/single-page-app \
;; :name donut-template-test/test-app \
;; :target-dir ../template-test
