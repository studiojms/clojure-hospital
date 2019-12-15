(ns clojure-hospital.multi-methods-no-record)

(defn authorize-function
  "Function responsible for deciding if the term needs to be signed"
  [order]
  (let [patient (:patient order)
        situation (:situation patient)]
    (cond (= :urgent situation) :always-authorized
          (contains? patient :health-plan) :health-plan
          :else :minimum-credit)))

(defmulti must-sign-pre-authorization-term? authorize-function)

(defmethod must-sign-pre-authorization-term? :always-authorized
  [order]
  false)

(defmethod must-sign-pre-authorization-term? :health-plan
  [order]
  (not (some #(= % (:procedure order)) (:health-plan (:patient order)))))

(defmethod must-sign-pre-authorization-term? :minimum-credit
  [order]
  (>= (:value order 0) 50))

(let [private-patient {:id 1 :name "John" :birth-date "01/01/1980" :situation :normal}
      health-plan-patient {:id 4 :name "Mary" :birth-date "05/12/1986" :situation :normal :procedure [:x-ray]}]
  (println (must-sign-pre-authorization-term? {:patient private-patient :procedure :x-ray :value 400}))
  (println (must-sign-pre-authorization-term? {:patient private-patient :procedure :x-ray :value 40}))
  (println (must-sign-pre-authorization-term? {:patient health-plan-patient :procedure :blood-sample :value 9999}))
  (println (must-sign-pre-authorization-term? {:patient health-plan-patient :procedure :x-ray :value 9999})))