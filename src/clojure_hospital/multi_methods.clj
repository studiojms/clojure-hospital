(ns clojure-hospital.multi-methods)

(defrecord PrivatePatient [id name birth-date situation])
(defrecord HealthPlanPatient [id name birth-date situation health-plan])

(defn authorize-function
  "Function responsible for deciding if the term needs to be signed"
  [order]
  (let [patient (:patient order)
        situation (:situation patient)
        urgent? (= :urgent situation)]
    (if urgent?
      :always-authorized
      (class patient))))

(defmulti must-sign-pre-authorization-term-order? authorize-function)

(defmethod must-sign-pre-authorization-term-order? :always-authorized
  [order]
  false)

(defmethod must-sign-pre-authorization-term-order? PrivatePatient
  [order]
  (>= (:value order 0) 50))

(defmethod must-sign-pre-authorization-term-order? HealthPlanPatient
  [order]
  (not (some #(= % (:procedure order)) (:health-plan (:patient order)))))

(let [private-patient (->PrivatePatient 1 "John" "01/01/1980" :urgent)
      health-plan-patient (->HealthPlanPatient 4 "Mary" "05/12/1986" :urgent [:x-ray])]
  (println (must-sign-pre-authorization-term-order? {:patient private-patient :procedure :x-ray :value 400}))
  (println (must-sign-pre-authorization-term-order? {:patient private-patient :procedure :x-ray :value 40}))
  (println (must-sign-pre-authorization-term-order? {:patient health-plan-patient :procedure :blood-sample :value 9999}))
  (println (must-sign-pre-authorization-term-order? {:patient health-plan-patient :procedure :x-ray :value 9999})))