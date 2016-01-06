(ns halting.tapes
  (:refer-clojure :exclude [read]))

(def zero-tape
  "The tape filled with 0's."
  [0 [nil 0] [nil 0]])

(defn read
  "Reads a 0 or a 1 from the current location on the tape."
  [[at]]
  at)

(defn write
  "Writes a 0 or a 1 to the current location on the tape."
  [[_ left right] value]
  [value left right])
