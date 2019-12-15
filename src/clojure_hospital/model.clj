(ns clojure-hospital.model
  (:require [schema.core :as s]))

(def empty-queue clojure.lang.PersistentQueue/EMPTY)

(defn new-hospital
  []
  {:waiting-queue empty-queue
   :lab1-queue empty-queue
   :lab2-queue empty-queue
   :lab3-queue empty-queue})

(s/def PatientId s/Str)
(s/def Department (s/queue PatientId))
(s/def Hospital {s/Keyword Department})
