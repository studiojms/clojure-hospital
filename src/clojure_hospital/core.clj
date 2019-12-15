(ns clojure-hospital.core
  (:use [clojure pprint])
  (:require [clojure-hospital.model :as c.model]))

(let [my-hospital (c.model/new-hospital)]
  (pprint my-hospital))

(pprint c.model/empty-queue)