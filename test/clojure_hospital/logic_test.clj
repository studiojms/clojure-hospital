(ns clojure-hospital.logic-test
  (:require [clojure.test :refer :all]
            [clojure-hospital.logic :refer :all]
            [clojure-hospital.model :as c.model]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer (defspec)]
            [schema-generators.generators :as g]
            [schema.core :as s]
            [clojure.string :as str])
  (:import (clojure.lang ExceptionInfo)))

(s/set-fn-validation! true)

(deftest can-add?-test
  (testing "Can add elements"
    (is (can-add? {:waiting-queue []} :waiting-queue)))

  (testing "Cannot add when queue is full"
    (is (not (can-add? {:waiting-queue [1 2 3 4 5]} :waiting-queue))))

  (testing "Cannot add when queue has more elements than full"
    (is (not (can-add? {:waiting-queue [1 2 3 4 5 6]} :waiting-queue))))

  (testing "Can add elements when there is an empty space"
    (is (can-add? {:waiting-queue [1 2 3 4]} :waiting-queue)))

  (testing "Cannot add when queue doesn't exists"
    (is (not (can-add? {:waiting-queue [1 2 3]} :test))))

  (testing "Can add people in queues with four elements"
    (doseq [queue (gen/sample (gen/vector gen/string-alphanumeric 0 4))]
      (is (can-add? {:waiting-queue queue} :waiting-queue)))))

(deftest arrived-at-test
  (testing "Should accept elements when it is not full"
    (is (= {:waiting-queue [1 2 3 4 5]} (arrived-at {:waiting-queue [1 2 3 4]} :waiting-queue 5)))))

(deftest transfer-test
  (testing "Accept person if there is an open position"
    (let [hospital {:waiting-queue (conj c.model/empty-queue "13" "22") :x-ray (conj c.model/empty-queue "5")}]
      (is (= ({:waiting-queue ["13"] :x-ray ["5" "22"]}
              (transfer hospital :waiting-queue :x-ray))))))

  (testing "Reject person if there is not an open position"
    (let [hospital {:waiting-queue (conj c.model/empty-queue 13 22) :x-ray (conj c.model/empty-queue 5 6 54 345 146)}]
      (is (thrown? ExceptionInfo (transfer hospital :waiting-queue :x-ray)))))

  (testing "Cannot call transfer with an invalid department"
    (let [hospital {:waiting-queue (conj c.model/empty-queue 13 22) :x-ray (conj c.model/empty-queue 5 6 54 345)}]
      (is (thrown? ExceptionInfo (transfer hospital :no-queue :waiting-queue))))))

(defspec can-add-item-in-queues-with-less-than-five-items 10
         (prop/for-all
           [queue (gen/vector gen/string-alphanumeric 0 4)
            person gen/string-alphanumeric]
           (is (= {:waiting-queue (conj queue person)}
                  (arrived-at {:waiting-queue queue} :waiting-queue person)))))

(def random-name-gen (gen/fmap str/join
                               (gen/vector gen/char-alphanumeric 5 10)))

(def non-full-queue-gen (gen/fmap #(reduce conj c.model/empty-queue %)
                                  (gen/vector random-name-gen 0 4)))

(defn do-transfer-ignoring-errors
  [hospital to]
  (try
    (transfer hospital :waiting-queue to)
    (catch IllegalStateException e
      hospital)))


(defspec transfer-must-maintain-the-amount-of-people 10
         (prop/for-all
           [waiting non-full-queue-gen
            x-ray non-full-queue-gen
            ultrasound non-full-queue-gen
            go-to (gen/vector (gen/elements [:x-ray :ultrasound]) 10 50)]
           (let [initial-hospital {:waiting-queue waiting
                                   :x-ray         x-ray
                                   :ultrasound    ultrasound}
                 final-hospital (reduce do-transfer-ignoring-errors initial-hospital go-to)]
             (= (count-patients initial-hospital) (count-patients final-hospital)))))

(defn add-waiting-queue
  [[hospital queue]]
  (assoc hospital :waiting-queue queue))

(def hospital-gen
  (gen/fmap add-waiting-queue
            (gen/tuple
              (gen/not-empty (g/generator c.model/Hospital))
              non-full-queue-gen)))

(def arrived-at-gen
  (gen/tuple
    (gen/return arrived-at)
    (gen/return :waiting-queue)
    random-name-gen
    (gen/return 1)))

(defn add-nonexistent-to-department-name [department]
  (keyword (str department "-nonexistent")))

(defn transfer-gen [hospital]
  (let [departments (keys hospital)
        nonexistent-departments (map add-nonexistent-to-department-name departments)
        all-departments (concat departments nonexistent-departments)]
    (gen/tuple (gen/return transfer)
               (gen/elements all-departments)
               (gen/elements all-departments)
               (gen/return 0))))

(defn action-gen [hospital]
  (gen/one-of [arrived-at-gen (transfer-gen hospital)]))

(defn actions-gen [hospital]
  (gen/not-empty (gen/vector (action-gen hospital) 1 100)))

(defn exec-action
  [situation [function param1 param2 delta-if-success]]
  (let [hospital (:hospital situation)
        current-delta (:delta situation)]
    (try
      (let [new-hospital (function hospital param1 param2)]
        {:hospital new-hospital
         :delta    (+ delta-if-success current-delta)})
      (catch IllegalStateException e
        situation)
      (catch AssertionError e
        situation))))

(defspec simulates-one-day-of-hospital 5
         (prop/for-all
           [initial-hospital hospital-gen]
           (let [actions (gen/generate (actions-gen initial-hospital))
                 initial-situation {:hospital initial-hospital :delta 0}
                 initial-patients-count (count-patients initial-hospital)
                 final-situation (reduce exec-action initial-situation actions)
                 final-patients-count (count-patients (:hospital final-situation))]
             (println final-patients-count initial-patients-count (:delta final-situation))
             (is (= (- final-patients-count (:delta final-situation) final-patients-count))))))
