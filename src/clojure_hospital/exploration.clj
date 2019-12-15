(ns clojure-hospital.exploration
  (:use [clojure pprint])
  (:require [clojure-hospital.model :as c.model]
            [clojure-hospital.logic :as c.logic]))

(defn simulate-day []
  (def hospital (c.model/new-hospital))
  (def hospital (c.logic/arrived-at hospital :waiting-queue "123"))
  (def hospital (c.logic/arrived-at hospital :waiting-queue "345"))

  (def hospital (c.logic/arrived-at hospital :lab1-queue "533"))
  (def hospital (c.logic/arrived-at hospital :lab1-queue "534"))

  (def hospital (c.logic/arrived-at hospital :lab2-queue "644"))

  (def hospital (c.logic/arrived-at hospital :lab3-queue "655"))
  (pprint hospital)

  (def hospital (c.logic/receive hospital :waiting-queue))
  (def hospital (c.logic/receive hospital :waiting-queue))

  (def hospital (c.logic/arrived-at hospital :waiting-queue "111"))
  (def hospital (c.logic/arrived-at hospital :waiting-queue "222"))
  (def hospital (c.logic/arrived-at hospital :waiting-queue "333"))
  (def hospital (c.logic/arrived-at hospital :waiting-queue "444"))
  (def hospital (c.logic/arrived-at hospital :waiting-queue "555"))
  (def hospital (c.logic/arrived-at hospital :waiting-queue "666"))

  (pprint hospital))

;(simulate-day)

(defn simulate-parallel []
  (let [hospital (atom (c.model/new-hospital))]

    (.start (Thread. (fn [] (swap! hospital c.logic/arrived-at-paused :waiting-queue "111"))))
    (.start (Thread. (fn [] (swap! hospital c.logic/arrived-at-paused :waiting-queue "222"))))
    (.start (Thread. (fn [] (swap! hospital c.logic/arrived-at-paused :waiting-queue "333"))))
    (.start (Thread. (fn [] (swap! hospital c.logic/arrived-at-paused :waiting-queue "444"))))
    (.start (Thread. (fn [] (swap! hospital c.logic/arrived-at-paused :waiting-queue "555"))))
    (.start (Thread. (fn [] (swap! hospital c.logic/arrived-at-paused :waiting-queue "666"))))
    (.start (Thread. (fn [] (Thread/sleep 5000)
                       (pprint @hospital))))))

(simulate-parallel)

;;;;;;;;;;; atoms

(let [hospital (atom {:waiting-queue c.model/empty-queue})]
  (println "-------")
  (pprint (deref hospital))
  (pprint @hospital)

  (swap! hospital assoc :lab1-queue c.model/empty-queue)
  (pprint @hospital)

  (swap! hospital update :waiting-queue conj "user-01")
  (pprint @hospital))

