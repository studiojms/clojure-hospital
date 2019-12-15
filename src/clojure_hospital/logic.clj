(ns clojure-hospital.logic
  (:require [schema.core :as s]
            [clojure-hospital.model :as h.model]))

(defn can-add? [hospital department]
  (some-> hospital
          (get department)
          count
          (< 5)))

(defn arrived-at
  [hospital department person]
  (if (can-add? hospital department)
    (update hospital department conj person)
    (throw (IllegalStateException. "Queue is full!"))))
    ;(throw (ex-info "Queue is full!" {:hospital hospital :department department}))))

(s/defn receive :- h.model/Hospital
  [hospital :- h.model/Hospital department :- s/Keyword]
  (update hospital department pop))

(s/defn next-patient :- (s/maybe h.model/PatientId)
  [hospital :- h.model/Hospital department :- s/Keyword]
  (-> hospital
      department
      peek))

(s/defn transfer :- h.model/Hospital
  [hospital :- h.model/Hospital from :- s/Keyword to :- s/Keyword]
  {
   :pre [(contains? hospital from)]}
  (if-let [person (next-patient hospital from)]
    (-> hospital
        (receive from)
        (arrived-at to person))
    hospital))

(defn arrived-at-paused
  [hospital department person]
  (Thread/sleep (* (rand) 2000))
  (if (can-add? hospital department)
    (update hospital department conj person)
    (throw (ex-info "Queue is full!" {:hospital hospital :department department}))))

(defn count-patients
  [hospital]
  (reduce + (map count (vals hospital))))
