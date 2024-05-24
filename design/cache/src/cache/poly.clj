(ns cache.poly
  {:typed.clojure {:experimental #{:cache}}}
  (:require [typed.clojure :as t]))

(t/defalias As (t/Seqable t/Int))
(t/defalias Bs (t/Seqable t/Bool #_t/Int)) ;; changing this alias should trigger rechecking all defn's in this namespace

(t/ann-many [As :-> Bs]
            map-kw1
            map-kw2
            map-kw3
            map-kw4
            map-kw5
            map-kw6
            map-kw7
            map-kw8
            map-kw9
            map-kw10
            map-kw11
            map-kw12
            map-kw13
            map-kw14
            map-kw15
            map-kw16
            map-kw17
            map-kw18
            map-kw19)
(defn map-kw1 [as] ^{::t/dbg "map-kw1"} (map boolean as))
(defn map-kw2 [as] ^{::t/dbg "map-kw2"} (map boolean as))
(defn map-kw3 [as] ^{::t/dbg "map-kw3"} (map boolean as))
(defn map-kw4 [as] ^{::t/dbg "map-kw4"} (map boolean as))
(defn map-kw5 [as] ^{::t/dbg "map-kw5"} (map boolean as))
(defn map-kw6 [as] ^{::t/dbg "map-kw6"} (map boolean as))
(defn map-kw7 [as] ^{::t/dbg "map-kw7"} (map boolean as))
(defn map-kw8 [as] ^{::t/dbg "map-kw8"} (map boolean as))
(defn map-kw9 [as] ^{::t/dbg "map-kw9"} (map boolean as))
(defn map-kw10 [as] ^{::t/dbg "map-kw10"} (map boolean as))
(defn map-kw11 [as] ^{::t/dbg "map-kw11"} (map boolean as))
(defn map-kw12 [as] ^{::t/dbg "map-kw12"} (map boolean as))
(defn map-kw13 [as] ^{::t/dbg "map-kw13"} (map boolean as))
(defn map-kw14 [as] ^{::t/dbg "map-kw14"} (map boolean as))
(defn map-kw15 [as] ^{::t/dbg "map-kw15"} (map boolean as))
(defn map-kw16 [as] ^{::t/dbg "map-kw16"} (map boolean as))
(defn map-kw17 [as] ^{::t/dbg "map-kw17"} (map boolean as))
(defn map-kw18 [as] ^{::t/dbg "map-kw18"} (map boolean as))
(defn map-kw19 [as] ^{::t/dbg "map-kw19"} (map boolean as))

(comment
  ;; 3x faster
  (time (t/check-ns-clj *ns* :max-parallelism :available-processors))

  (user/debugging-tools)
  (time+ 5000 (with-out-str (t/cns)))
  (prof/profile {:event :alloc} (time+ 10000 (with-out-str (t/cns))))

  (prof/generate-diffgraph 3 4  {})

  (prof/serve-ui 8085)

  ;; Offenders:

  typed.cljc.checker.cs-gen/subtype?

  typed.clj.checker.subtype/subtype-RClass

  typed.clj.checker.subtype/subtypeA*

  typed.cljc.checker.type-ctors/overlap

  typed.cljc.checker.type-ctors/return-if-changed

  typed.cljc.checker.type-ctors/TypeFn-bbnds*

  typed.clj.checker.subtype/subtype-TApp

  typed.cljc.checker.check.cache/record-cache!

  typed.cljc.analyzer/eval-top-level

  clojure.core.typed.coerce-utils/symbol->Class

  typed.cljc.checker.type-ctors/instantiate-many

  typed.cljc.checker.type-ctors/instantiate-typefn

  typed.cljc.checker.subst/substitute-many

  typed.cljc.checker.cs-gen/cset-meet*

  typed.cljc.checker.name-env/resolve-name*

  typed.clj.checker.rclass-ancestor-env/rclass-replacements

  typed.cljc.checker.type-ctors/fully-resolve-type

  typed.cljc.checker.cs-gen/cs-gen
  
  (set! *assert* false)
  )
