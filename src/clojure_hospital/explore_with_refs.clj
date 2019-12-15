(ns clojure-hospital.explore-with-refs
  (:use [clojure-hospital.model :as c.model]))

(defn can-add-to-queue? [queue]
  (-> queue
      count
      (< 5)))

(defn arrive-at
  [queue person]
  (if (can-add-to-queue? queue)
    (conj queue person)
    (throw (ex-info "Queue is full!" {:queue queue}))))

(defn arrive-at!
  "Changes references using alter"
  [hospital person]
  (let [queue (get hospital :waiting-queue)]
    (alter queue arrive-at person)))


(defn simulate-day
  []
  (let [hospital {:waiting-queue (ref c.model/empty-queue)
                  :lab1-queue (ref c.model/empty-queue)
                  :lab2-queue (ref c.model/empty-queue)
                  :lab3-queue (ref c.model/empty-queue)}]
    (dosync
      (arrive-at! hospital "john")
      (arrive-at! hospital "mary")
      (arrive-at! hospital "andrea")
      (arrive-at! hospital "gerald")
      (arrive-at! hospital "james")
      (arrive-at! hospital "tony"))
    (clojure.pprint/pprint hospital)))

;(simulate-day)

(defn async-arrive-at!
  [hospital person]
  (future (Thread/sleep (rand 5000))
          (dosync
            (println "trying sync code for person " person)
            (arrive-at! hospital person))))

(defn simulate-day-async
  []
  (let [hospital {:waiting-queue (ref c.model/empty-queue)
                  :lab1-queue (ref c.model/empty-queue)
                  :lab2-queue (ref c.model/empty-queue)
                  :lab3-queue (ref c.model/empty-queue)}
        futures (mapv #(async-arrive-at! hospital %) (range 10))]
    (future
      (dotimes [person 10]
        (Thread/sleep 2000)
        (clojure.pprint/pprint hospital)
        (clojure.pprint/pprint futures)
        (async-arrive-at! hospital person)))
    (clojure.pprint/pprint hospital)))

(simulate-day-async)