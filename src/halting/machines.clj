(ns halting.machines)

;; (require '[clojure.test.check.generators :as gen])
;; (require '[clojure.set :as sets])

;; (defn gen-TM-state
;;   [total-states]
;;   (gen/vector (gen/tuple (gen/choose 0 1)
;;                          (gen/elements [:left :right])
;;                          (gen/choose 0 total-states))
;;               2))

;; (def gen-TM
;;   (gen/bind gen/nat
;;             (fn [state-count]
;;               (gen/vector (gen-TM-state state-count) state-count))))

(def the-empty-machine
  [])

(defn number-of-state-specs
  "The number of possible specifications of a single state,
  given the total number of states in the machine."
  [state-count]
  (let [x (inc state-count)] (* x x 16)))

(defn total-machines-for-state-count
  [state-count]
  (apply *'
         (repeat state-count (number-of-state-specs state-count))))

;; ordering is the natural lexicographic ordering on the
;; representation we have
(defn nat->machine
  [n]
  {:pre [(integer? n) (not (neg? n))]}
  (loop [state-count 0
         n n]
    (let [machine-count (total-machines-for-state-count state-count)]
      (if (< n machine-count)
        (let [base (number-of-state-specs state-count)

              state-indices
              (loop [n n
                     indices ()]
                (if (= (count indices) state-count)
                  indices
                  (recur (quot n base)
                         (cons (mod n base) indices))))

              to-states (inc state-count)]
          (mapv (fn [index]
                  ;; shut up
                  (let [state1 (mod index to-states)
                        index (quot index to-states)
                        dir1 (mod index 2)
                        index (quot index 2)
                        write1 (mod index 2)
                        index (quot index 2)
                        state0 (mod index to-states)
                        index (quot index to-states)
                        dir0 (mod index 2)
                        index (quot index 2)
                        write0 (mod index 2)]
                    [[(long write0) ([:left :right] dir0) (long state0)]
                     [(long write1) ([:left :right] dir1) (long state1)]]))
                state-indices))
        (recur (inc state-count) (- n machine-count))))))

(defn machine->nat
  [machine]
  ;; TODO
  )

(defn inc-machine
  [machine]
  ;; TODO
  )

(defn permutations
  [coll]
  (if (empty? coll)
    [()]
    (for [x coll
          xs (permutations (remove #{x} coll))]
      (cons x xs))))

(defn TM-permutations
  [TM]
  (let [normal-states (dec (count TM))]
    (if (<= normal-states 1)
      [TM]
      (for [perm (permutations (range 1 (count TM)))]
        (mapv (fn [reads]
                (mapv (fn [[write dir state]]
                        [write dir
                         (if (or (zero? state)
                                 (= (count TM) state))
                           state
                           (nth perm (dec state)))])
                      reads))
              (cons (first TM)
                    (map TM perm)))))))

(defn canonical?
  "Returns true if the states are labeled canonically, and halting
  actions always write 0 and move left."
  [TM]
  (and (every? (fn [TM']
                 (not (pos? (compare TM TM'))))
               (TM-permutations TM))
       (every? (fn [reads]
                 (every? (fn [[write dir state]]
                           (or (< state (count TM))
                               (and (= write 0)
                                    (= dir :left))))
                         reads))
               TM)))
