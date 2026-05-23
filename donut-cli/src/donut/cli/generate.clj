(ns donut.cli.generate
  #_:clj-kondo/ignore 
  (:require
   [babashka.cli :as cli]
   [bling.core :as bling]
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
  {:generator-name {:alias  :g
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
  (println (cli/format-opts (merge spec {:order (vec (keys (:spec spec)))}))))

(defn generate
  [{:keys [generator-name entity-name]}]
  (let [full-generator-name (keyword (or (namespace generator-name) "donut.generators")
                                     (name generator-name))]
    (dg/generate full-generator-name
                 {:top         (proj/project-name)
                  :entity-name entity-name}
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
