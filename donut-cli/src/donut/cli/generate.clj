(ns donut.cli.generate
  (:require
   [bling.core :as bling]
   [donut.generate :as dg]
   [donut.generators :as dgg]))

;; TODO
;; - project root
;; - top
;; - pass in entity name

(defn info
  [{:keys [event-id point]}]
  (when (= :write-point event-id)
    (let [action                   (if (:modify point) "updating" "creating")
          {:keys [namespace path]} (:destination point)]
      (bling/print-bling [:bold.positive action] " "
                         (or namespace path) " "
                         [:subtle (:id point)]))))

(defn -main
  []
  (dg/generate ::dgg/entity-scaffold
               {:top         'donut-template-test/test-app
                :entity-name 'user}
               {:handle-info  info
                :handle-error dg/handle-error-log}))
