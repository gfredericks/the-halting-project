(ns halting.tapes
  (:refer-clojure :exclude [read]))

;;
;; run length encoding of infinite (eventually homogeneous) sequences
;;

(defn rle-repeat
  [x]
  (list [nil x]))

(defn rle-first
  [rle]
  (-> rle first second))

(defn rle-cons
  [x rle]
  (if (= (rle-first rle) x)
    (->> rle rest (cons (update (first rle) 0 #(some-> % inc))))
    (cons [1 x] rle)))

(defn rle-rest
  [rle]
  (if (= 1 (ffirst rle))
    (rest rle)
    (->> rle rest (cons (update (first rle) 0 #(some-> % dec))))))

;;
;; Infinite tape; a zipper using the run-length encoding
;;

(def zero-tape
  "The tape filled with 0's."
  [(rle-repeat 0) 0 (rle-repeat 0)])

(defn read
  "Reads a 0 or a 1 from the current location on the tape."
  [[_ at]]
  at)

(defn write
  "Writes a 0 or a 1 to the current location on the tape."
  [[left _ right] value]
  [left value right])

(defn move-left
  [[left at right]]
  [(rle-rest left) (rle-first left) (rle-cons at right)])

(defn move-right
  [[left at right]]
  [(rle-cons at left) (rle-first right) (rle-rest right)])
