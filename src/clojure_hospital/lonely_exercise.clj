(ns clojure-hospital.lonely-exercise
  (:use clojure.pprint))

(defn add-patient
  [patients patient]
  (let [id (:id patient)]
    (assoc patients id patient)))

(defn test-using-patient []
  (let [patients {}
        john {:id 10 :name "John" :birth-date "01/01/2000"}
        mary {:id 11 :name "Mary" :birth-date "15/11/1999"}
        paul {:id 12 :name "Paul" :birth-date "22/03/2002"}]

    (pprint (add-patient patients john))))

(test-using-patient)