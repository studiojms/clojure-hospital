(ns clojure-hospital.schemas
  (:require [schema.core :as s]))

(defn strictly-positive?
  "Returns true if the number is positive and not equal to zero"
  [number]
  (> number 0))

(s/set-fn-validation! true)

(def PositiveInt
  (s/pred pos-int?))

(def HealthPlan [s/Keyword])

(def Patient
  "Defines a schema for Patient"
  {:id PositiveInt
   :name s/Str
   :plan HealthPlan
   (s/optional-key :birth-date) s/Str})

(s/defn new-patient :- Patient
  [id :- PositiveInt
   name :- s/Str
   plan :- HealthPlan]
  {:id id :name name :plan plan})

(println (new-patient 10 "test" [:x-ray]))
;(println (new-patient -10 "test"))

(defn greater-than-zero?
  [x]
  (>= x 0))

(def Financial (s/constrained s/Num greater-than-zero?))

(def Order
  {:patient Patient
   :value Financial
   :procedure s/Keyword})

(s/defn new-order :- Order
  [patient :- Patient
   value :- Financial
   procedure :- s/Keyword]
  {:patient patient :value value :procedure procedure})

(println (new-order (new-patient 12 "John" [:x-ray]) 15.2 :x-ray))

(def Patients
  {PositiveInt Patient})

(def Visits
  {PositiveInt [s/Str]})