(ns typed.clj.annotator.test.runtime-infer.gen-arglists
  {:lang :core.typed
   :core.typed {:features #{:runtime-infer}}
   }
  (:refer-clojure :exclude [*])
  (:require [typed.clojure :as t]
            [clojure.core :as core]
            [clojure.spec.alpha :as s]
            [clojure.pprint :refer [pprint]]))

;; Start: Generated by clojure.core.typed - DO NOT EDIT
(s/fdef * :args (s/cat :x int? :xs-0 int?) :ret int?)
(s/fdef
  function
  :args
  (s/alt :1-arg (s/cat :a int?) :2-args (s/cat :a int? :b-0 int?))
  :ret
  int?)
(s/fdef
  function2
  :args
  (s/alt :1-arg (s/cat :a int?) :2-args (s/cat :a int? :b-0 int?))
  :ret
  int?)
;; End: Generated by clojure.core.typed - DO NOT EDIT
(def function 
  (fn 
    ([a] a)
    ([a & b] a)))

(def *
  (fn
    ([] 1)
    ([x] x)
    ([x & xs]
     (reduce (fnil core/* 0M 0M) x xs))))

(def function2 function)

(function 1)
(function 1 2)

(function2 1 2)
(function2 1)

(* 2 3)
