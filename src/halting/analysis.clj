(ns halting.analysis
  (:require [halting.machines :as machines]
            [halting.tapes :as tapes]))

(defn reachable-states
  [TM]
  (loop [to-visit [0]
         visited #{}]
    (if (empty? to-visit)
      visited
      (let [to-states (-> TM (get (peek to-visit)) (->> (map last)))
            visited' (conj visited (peek to-visit))]
        (recur (-> to-visit pop (into (remove visited' to-states)))
               visited')))))

(defn degenerate?
  [TM]
  (= (inc (count TM))
     (count (reachable-states TM))))

(defn reachable-halt-state?
  [TM]
   (contains? (reachable-states TM) (count TM)))

(defn all-TMs
  "Returns a naive list of all TMs with N states (excluding halt
  state)."
  [N]
  (letfn [(TMs [state-count]
            (if (zero? state-count)
              [[]]
              (for [TM (TMs (dec state-count))
                    write0 [0 1]
                    dir0 [:left :right]
                    state0 (range (inc N))
                    write1 [0 1]
                    dir1 [:left :right]
                    state1 (range (inc N))]
                (conj TM [[write0 dir0 state0]
                          [write1 dir1 state1]]))))]
    (TMs N)))

(defn run-on-empty-tape
  "Returns the number of transitions to the halt state, or nil if
  max-steps was reached without halting."
  [TM max-steps]
  (let [halt-state (count TM)]
    (loop [tape tapes/zero-tape
           state 0
           steps 0]
      (cond (= halt-state state)
            steps

            (= steps max-steps)
            nil

            :else
            (let [[write dir state'] (get-in TM [state (tapes/read tape)])
                  steps' (inc steps)]
              (recur (-> tape
                         (tapes/write write)
                         ((case dir
                            :left tapes/move-left
                            :right tapes/move-right)))
                     state'
                     steps'))))))

(defn eventual-loop?
  "Returns true if a loop can be detected in max-steps by
  comparing the semi-local state of the tape with the last
  time the machine was in the current state."
  [TM max-steps window-size]
  (let [halt-state (count TM)]
    (loop [tape tapes/zero-tape
           state 0
           step 0
           pos 0
           states-last-seen {}]
      (cond (= halt-state state)
            false

            (= step max-steps)
            false

            (if-let [{last-tape :tape, last-pos :pos, last-step :step}
                     (states-last-seen [state (tapes/context tape window-size window-size)])]
              (if (= pos last-pos)
                (= last-tape tape)
                (let [Δstep (- step last-step)]
                  (if (< pos last-pos)
                    ;; moving left
                    (= (tapes/context tape nil Δstep)
                       (tapes/context last-tape nil Δstep))
                    ;; moving right
                    (= (tapes/context tape Δstep nil)
                       (tapes/context last-tape Δstep nil))))))
            true

            :else
            (let [[write dir state'] (get-in TM [state (tapes/read tape)])
                  step' (inc step)]
              (recur (-> tape
                         (tapes/write write)
                         ((case dir
                            :left tapes/move-left
                            :right tapes/move-right)))
                     state'
                     step'
                     ((case dir :left dec :right inc) pos)
                     (assoc states-last-seen [state (tapes/context tape window-size window-size)]
                            {:tape tape :pos pos :step step})))))))

(defn unidirectional?
  [TM]
  (->> TM
       (map first)
       (map second)
       (apply =)))

(defn oneless?
  "Returns true if the machine never writes a 1 and can only
  halt if it reads a 1."
  [TM]
  (and (->> TM
            (map ffirst)
            (every? zero?))
       (->> TM
            (map first)
            (map last)
            (not-any? #{(count TM)}))))

(defn trivial-loop?
  [TM]
  (-> TM first first last zero?))

(defn classify
  [TM]
  (cond (empty? TM)
        :halts-by-definition

        (not (machines/canonical? TM))
        :uncanonical

        (trivial-loop? TM)
        :infinite-by-immediate-loop

        (not (reachable-halt-state? TM))
        :unreachable-halt-state

        (run-on-empty-tape TM (* 10 (count TM)))
        :halts-quickly

        (oneless? TM)
        :infinite-by-onelessness

        (unidirectional? TM)
        :infinite-by-unidirectionality

        (eventual-loop? TM (* 100 (count TM)) 1)
        :eventual-loop

        :else
        :unknown))

(defn classification-summary
  []
  (->> (range)
       (map (juxt identity machines/nat->machine))
       (reduce (fn [{:keys [result seen] :as state} [n machine]]
                 (let [class (classify machine)]
                   (if (seen class)
                     (if (-> result peek first (= :old))
                       (update-in state [:result (dec (count result)) 1 class]
                                  (fnil inc 0))
                       (update state :result conj [:old {class 1}]))
                     (cond-> {:result (conj result [:new class n machine])
                              :seen (conj seen class)}
                       (= :unknown class)
                       (reduced)))))
               {:result []
                :seen #{}})
       (:result)))
