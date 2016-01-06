(ns halting.machines-test
  (:require [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [halting.analysis :as analysis]
            [halting.machines :as machines]))

(defn gen-TM-state
  [total-states]
  (gen/vector (gen/tuple (gen/choose 0 1)
                         (gen/elements [:left :right])
                         (gen/choose 0 total-states))
              2))

(defn gen-TM*
  [state-count]
  (gen/vector (gen-TM-state state-count) state-count))

(def gen-TM
  (gen/bind gen/nat gen-TM*))

(def gen-small-TM
  (gen/bind (gen/large-integer* {:min 0 :max 6})
            gen-TM*))

(defspec bijection-spec-1
  (prop/for-all [nat (gen/large-integer* {:min 0})]
    (-> nat machines/nat->machine machines/machine->nat
        (= nat))))

(defspec bijection-spec-2
  (prop/for-all [machine gen-TM]
    (-> machine machines/machine->nat machines/nat->machine
        (= machine))))

(defspec canonization-spec
  (prop/for-all [machine gen-small-TM]
    (<= (-> machine machines/canonize machines/machine->nat)
        (-> machine machines/machine->nat))))

(defspec canonize-preserves-behavior-spec
  (prop/for-all [machine gen-small-TM]
    (= (analysis/run-on-empty-tape machine 1000)
       (analysis/run-on-empty-tape (machines/canonize machine) 1000))))
