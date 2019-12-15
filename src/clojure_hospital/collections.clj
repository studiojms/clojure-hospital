(ns clojure-hospital.collections)

(defn test-vector []
  (let [wait [111 222]]
    (println wait)
    (println (conj wait 333))))

(defn test-list []
  (let [wait '(111 222)]
    (println wait)
    (println (conj wait 333))
    (println (pop wait))))

(test-list)

(defn test-queue []
  (let [wait (conj clojure.lang.PersistentQueue/EMPTY 111 222)]
    (println (seq wait))
    (println (seq (conj wait 333)))
    (println (seq (pop wait)))))

(test-queue)
