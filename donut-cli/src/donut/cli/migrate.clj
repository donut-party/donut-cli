(ns donut.cli.migrate
  (:require
   [babashka.cli :as cli]
   [babashka.process :as ps]))

(defn- migrate
  [& args]
  (apply ps/shell "clj" "-M:migrate" (map str args)))

(defn- no-args-handler [task]
  (fn [_] (migrate task)))

(defn- single-arg-handler [task opt-key]
  (fn [{:keys [opts]}]
    (if-let [v (get opts opt-key)]
      (migrate task v)
      (println (str "Usage: donut migrate " task " <" (name opt-key) ">")))))

(declare dispatch-table)

(defn- print-help [_]
  (println "Usage: donut migrate <command> [args]")
  (println "\nCommands:")
  (doseq [{:keys [cmds args desc]} dispatch-table]
    (when (seq cmds)
      (println (format "  %-28s %-16s %s"
                       (first cmds)
                       (or args "")
                       (or desc ""))))))

(def dispatch-table
  [{:cmds ["init"]
    :desc "Initialize the database for migrations"
    :fn   (no-args-handler "init")}

   {:cmds       ["create"]
    :args       "<name>"
    :desc       "Create a new migration file"
    :spec       {:name {:desc   "Name for the new migration"
                        :coerce :string}}
    :args->opts [:name]
    :fn         (single-arg-handler "create" :name)}

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
    :args       "<id>"
    :desc       "Rollback migrations until just after the given migration id"
    :spec       {:id {:desc   "Migration id"
                      :coerce :long}}
    :args->opts [:id]
    :fn         (single-arg-handler "rollback-until-just-after" :id)}

   {:cmds ["up"]
    :args "<id> [id ...]"
    :desc "Run specific migrations up by id"
    :fn   (fn [{:keys [rest-args]}]
            (if (seq rest-args)
              (apply migrate "up" rest-args)
              (println "Usage: donut migrate up <id> [id ...]")))}

   {:cmds ["down"]
    :args "<id> [id ...]"
    :desc "Run specific migrations down by id"
    :fn   (fn [{:keys [rest-args]}]
            (if (seq rest-args)
              (apply migrate "down" rest-args)
              (println "Usage: donut migrate down <id> [id ...]")))}

   {:cmds ["pending-list"]
    :desc "List all pending migrations"
    :fn   (no-args-handler "pending-list")}

   {:cmds       ["migrate-until-just-before"]
    :args       "<id>"
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
