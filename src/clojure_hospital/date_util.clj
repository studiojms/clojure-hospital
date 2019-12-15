(ns clojure-hospital.date-util)

(defprotocol Dateable
  (to-ms [value]))

(extend-type java.lang.Number
  Dateable
  (to-ms [value] value))

(extend-type java.util.Date
  Dateable
  (to-ms [value] (.getTime value)))

(extend-type java.util.Calendar
  Dateable
  (to-ms [value] (to-ms (.getTime value))))

(defn now
  []
  (to-ms (java.util.Date.)))