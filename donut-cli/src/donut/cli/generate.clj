(ns donut.cli.generate
  #_:clj-kondo/ignore
  (:require
   [babashka.cli :as cli]
   [bling.core :as bling]
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [donut.cli.deps :as deps]
   [donut.cli.util.donut-project :as proj]
   [donut.generate :as dg]
   [donut.generators :as dgg] ;; required for multimethod
   ))

(defn info
  [{:keys [event-id point]}]
  (when (= :write-point event-id)
    (let [action (if (:modify point) "updating" "creating")]
      (bling/print-bling [:bold.positive action] " "
                         [:subtle (dg/rendered-point-file-path point)] " "
                         (:description point)))))

(def cli-opts
  {:lib            {:alias :l
                    :desc  "library providing generators, as group/artifact:version (version optional)"}
   :generator-name {:alias  :g
                    :desc   "name of the generator"
                    :coerce :keyword}
   :entity-name    {:alias  :e
                    :desc   "name of the entity to generate a scaffold for"
                    :coerce :symbol}})

(def cli-spec
  {:spec       cli-opts
   :args->opts [:generator-name :entity-name]})

(defn print-help
  [spec]
  (println "generates code for your Donut app")
  (println "example: donut generate entity-scaffold book")
  (println "example: donut generate --lib party.donut/bakery:0.0.47 :donut.bakery.generators/install")
  (println (cli/format-opts (merge spec {:order (vec (keys (:spec spec)))}))))

(defn parse-lib
  "Parse a --lib value of the form group/artifact:version (version optional)
  into a `[lib-symbol version-or-nil]` pair."
  [lib-str]
  (let [[lib version] (str/split lib-str #":" 2)]
    [(symbol lib) version]))

(defn lib-resource-name
  "Resource path holding a lib's donut config, e.g. party.donut/bakery ->
  \"donut/party.donut.bakery.edn\"."
  [lib]
  (format "donut/%s.%s.edn" (namespace lib) (name lib)))

(defn load-lib-generators
  "Download `lib-str` (group/artifact:version, version optional), add it to the
  classpath, then require the generator namespaces it declares in its
  \"donut/group.artifact.edn\" resource under [:donut :generator-namespaces]."
  [lib-str]
  (let [[lib version] (parse-lib lib-str)]
    (if version
      (deps/add-dependency lib version)
      (deps/add-dependency-latest lib))
    (let [resource-name (lib-resource-name lib)
          resource      (io/resource resource-name)]
      (when-not resource
        (throw (ex-info (str "could not find resource " resource-name " on the classpath")
                        {:lib lib :resource resource-name})))
      (doseq [generator-ns (get-in (edn/read-string (slurp resource))
                                   [:donut :generator-namespaces])]
        (require generator-ns)))))

(defn generate
  [{:keys [lib generator-name entity-name]}]
  (when lib
    (load-lib-generators lib))
  (let [full-generator-name (keyword (or (namespace generator-name) "donut.generators")
                                     (name generator-name))
        [lib version]       (some-> lib parse-lib)]
    (dg/generate full-generator-name
                 {:top             (proj/project-name)
                  :entity-name     entity-name
                  :gen-lib         lib
                  :gen-lib-version version}
                 {:handle-info  info
                  :handle-error dg/handle-error-log})))

(defn -main
  [& args]
  (if args
    (let [opts (cli/parse-opts args cli-spec)]
      (if (or (:help opts) (:h opts))
        (print-help cli-spec)
        (generate opts)))
    (print-help cli-spec)))
