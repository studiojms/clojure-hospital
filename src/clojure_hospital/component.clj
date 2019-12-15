(ns clojure-hospital.component
  (:require [clojure-hospital.date-util :as c.date-util]))

(defn load-patient
  [id]
  (println "Loading patient " id)
  (Thread/sleep 1000)
  {:id id :loaded-in (c.date-util/now)})

(defn load-with-cache
  [cache id load-fn]
  (if (contains? cache id)
    cache
    (let [data (load-fn id)]
      (assoc cache id data))))

(println (load-with-cache {} 10 load-patient))

(println (load-with-cache {19 {:id 19}} 19 load-patient))


(defprotocol Loadable
  (load! [data id]))

(defrecord Cache
  [cache load-fn]
  Loadable
  (load! [data id]
    (swap! cache load-with-cache id load-fn)
    (get @cache id)))

(def patients (->Cache (atom {}) load-patient))

(load! patients 10)
(load! patients 14)
(load! patients 10)
(load! patients 16)