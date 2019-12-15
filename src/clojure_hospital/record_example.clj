(ns clojure-hospital.record-example)

(defn add-patient
  [patients patient]
  (if-let [id (get patient :id)]
    (assoc patients id patient)
    (throw (ex-info "Patient has no id" {:patient patient}))))

(defn test-add-patient
  []
  (let [patients {}
        john {:id 1 :name "John" :birth-date "10-11-2000"}
        mary {:id 2 :name "Mary" :birth-date "15-01-1988"}
        paul {:name "Paul" :birth-date "22-04-1978"}]
    (prn (add-patient patients john))
    (prn (add-patient patients mary))
    (prn (add-patient patients paul))))

;(test-add-patient)

(defrecord Patient [id name birth-date])

(->Patient 1 "John" "10-11-2000")