(ns donut.cli.migrate
  (:require
   [babashka.cli :as cli]
   [babashka.process :as proc]))

(defn- run!
  [& args]
  (apply proc/shell "clj" "-M:migrate" (map str args)))

(defn- no-args-handler [task]
  (fn [_] (run! task)))

(defn- single-arg-handler [task opt-key]
  (fn [{:keys [opts]}]
    (run! task (get opts opt-key))))

(declare dispatch-table)

(defn- print-help [_]
  (println "Usage: donut migrate <command> [options]")
  (println "\nCommands:")
  (doseq [{:keys [cmds desc]} dispatch-table]
    (when (seq cmds)
      (println (format "  %-35s %s" (first cmds) (or desc ""))))))

(def dispatch-table
  [{:cmds ["init"]
    :desc "Initialize the database for migrations"
    :fn   (no-args-handler "init")}

   {:cmds    ["create"]
    :desc    "Create a new migration file"
    :spec    {:name {:desc   "Name for the new migration"
                     :coerce :string}}
    :args->opts [:name]
    :fn     (single-arg-handler "create" :name)}

   {:cmds ["migrate"]
    :desc "Run all pending migrations"
    :fn   (no-args-handler "migrate")}

   {:cmds ["rollback"]
    :desc "Rollback the last migration"
    :fn   (no-args-handler "rollback")}

   {:cmds ["reset"]
    :desc "Rollback all migrations and re-run them"
    :fn   (no-args-handler "reset")}

   {:cmds       ["rollback-until-just-after"]
    :desc       "Rollback migrations until just after the given migration id"
    :spec       {:id {:desc   "Migration id"
                      :coerce :long}}
    :args->opts [:id]
    :fn         (single-arg-handler "rollback-until-just-after" :id)}

   {:cmds ["up"]
    :desc "Run specific migrations up by id"
    :fn   (fn [{:keys [rest-args]}]
            (apply run! "up" rest-args))}

   {:cmds ["down"]
    :desc "Run specific migrations down by id"
    :fn   (fn [{:keys [rest-args]}]
            (apply run! "down" rest-args))}

   {:cmds ["pending-list"]
    :desc "List all pending migrations"
    :fn   (no-args-handler "pending-list")}

   {:cmds       ["migrate-until-just-before"]
    :desc       "Run migrations until just before the given migration id"
    :spec       {:id {:desc   "Migration id"
                      :coerce :long}}
    :args->opts [:id]
    :fn         (single-arg-handler "migrate-until-just-before" :id)}

   {:cmds []
    :fn   print-help}])

(defn -main
  [& args]
  (cli/dispatch dispatch-table (vec args)))
