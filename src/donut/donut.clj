(ns donut.donut
  (:require
   [babashka.cli :as cli]
   [donut.cli.new :as new]))

(defn -main
  [& _args]
  (cli/dispatch
   [{:cmds ["new"] :fn new/run-deps-new :spec new/cli-opts}]
   *command-line-args*))
