(ns halting.machines)



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
  (->> (range (count machine))
       (map total-machines-for-state-count)
       (reduce +')
       (+ (let [base (number-of-state-specs (count machine))
                to-states (inc (count machine))]
            (->> machine
                 (map (fn [[[write0 dir0 state0]
                            [write1 dir1 state1]]]
                        (-> write0
                            (* 2)
                            (+ ({:left 0 :right 1} dir0))
                            (* to-states)
                            (+ state0)
                            (* 2)
                            (+ write1)
                            (* 2)
                            (+ ({:left 0 :right 1} dir1))
                            (* to-states)
                            (+ state1))))
                 (reduce #(-> %1 (*' base) (+' %2)) 0))))))

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
                           (inc (.indexOf ^java.util.List perm state)))])
                      reads))
              (cons (first TM)
                    (map TM perm)))))))

(defn canonize
  [TM]
  (let [halt-state (count TM)]
    (->> TM
         (mapv (fn [reads]
                 (mapv (fn [[write dir state :as spec]]
                         (if (= halt-state state)
                           [0 :left state]
                           spec))
                       reads)))
         (TM-permutations)
         (reduce (fn [tm1 tm2]
                   (if (neg? (compare tm1 tm2))
                     tm1
                     tm2))))))

(defn canonical?
  "Returns true if the states are labeled canonically, and halting
  actions always write 0 and move left."
  [TM]
  (= TM (canonize TM)))

(def greek
  "αβγδεζηθικλμνξπρστυφχψω")

(defn print-markdown
  [machine]
  (let [state-name (if (<= (count machine) (count greek))
                     (mapv str greek)
                     str)
        state-name (fn [state] (if (= state (count machine))
                                 "H"
                                 (state-name state)))]
    (println "| State | Read 0 |       | Read 1 |       |")
    (println "|------:|:-------|:------|:-------|-------|")
    (doseq [[[[write0 dir0 state0]
              [write1 dir1 state1]]
             state]
            (map vector machine (range))]
      (printf "| **%s** | %s     | **%s** | %s     | **%s** |\n"
              (state-name state)
              (case dir0
                :left  (str \← write0)
                :right (str write0 \→))
              (state-name state0)
              (case dir1
                :left  (str \← write1)
                :right (str write1 \→))
              (state-name state1)))))

(defn print-morphett
  "Prints the given machine in the syntax use at
  http://morphett.info/turing/turing.html."
  [machine]
  (let [symbol ["_" "1"]]
    (dotimes [state (count machine)]
      (let [[[write0 dir0 state0]
             [write1 dir1 state1]] (machine state)]
        (printf "%d %s %s %s %s\n"
                state (symbol 0) (symbol write0)
                ({:left "l" :right "r"} dir0) (if (= (count machine) state0) "halt" state0))
        (printf "%d %s %s %s %s\n"
                state (symbol 1) (symbol write1)
                ({:left "l" :right "r"} dir1) (if (= (count machine) state1) "halt" state1))))))
