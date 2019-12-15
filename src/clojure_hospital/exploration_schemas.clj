(ns clojure-hospital.exploration-schemas
  (:require [schema.core :as s]))

(defn add-patient
  [patients patient]
  (if-let [id (:id patient)]
    (assoc patients id patient)
    (throw (ex-info "Patient has no defined id" {:patient patient}))))

(defn schedule-visits
  [visits patient-id scheduled-visits]
  (if (contains? visits patient-id)
    (update visits patient-id concat scheduled-visits)
    (assoc visits patient-id scheduled-visits)))

(s/defn print-scheduled-visit
  [visits patient-id :- Long]
  (println "Schedules visit for patient id " patient-id "are" (get visits patient-id)))

(s/defn create-patient
  [id :- Long name :- s/Str]
  {:id id :name name})

(defn test-patients
  []
  (let [john {:id 1 :name "John"}
        mary {:id 2 :name "Mary"}
        patients (reduce add-patient {} [john mary])
        visits {}
        visits (schedule-visits visits 1 ["2019/11/19"])
        visits (schedule-visits visits 2 ["2020/12/01" "2020/12/10"])
        visits (schedule-visits visits 1 ["2020/09/07"])]
    (println patients)
    (println visits)
    (print-scheduled-visit visits 1)))

(test-patients)

(s/set-fn-validation! true)

(create-patient 1 "John")
;(println (create-patient "Ab" 333))