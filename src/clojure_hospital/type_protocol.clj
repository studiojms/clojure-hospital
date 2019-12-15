(ns clojure-hospital.type-protocol)

(defrecord PrivatePatient [id name birth-date])
(defrecord HealthPlanPatient [id name birth-date health-plan])

(defprotocol Chargeable
  (must-sign-pre-authorization-term? [patient procedure value]))

(extend-type PrivatePatient
  Chargeable
  (must-sign-pre-authorization-term? [patient procedure value]
    (>= value 50)))

(extend-type HealthPlanPatient
  Chargeable
  (must-sign-pre-authorization-term? [patient procedure value]
    (let [health-plan (get patient :health-plan)]
         (not (some #(= % procedure) health-plan)))))

(let [private-patient (->PrivatePatient 1 "John" "01/01/1980")
      health-plan-patient (->HealthPlanPatient 4 "Mary" "05/12/1986" [:x-ray])]
  (println (must-sign-pre-authorization-term? private-patient :x-ray 400))
  (println (must-sign-pre-authorization-term? private-patient :x-ray 40))
  (println (must-sign-pre-authorization-term? health-plan-patient :blood-sample 9999))
  (println (must-sign-pre-authorization-term? health-plan-patient :x-ray 9999)))