(ns clojure.core.typed.test.self-check-tc
  (:require [clojure.test :refer :all]
            [typed.clj.checker :refer [check-ns4]]
            [clojure.core.typed :as t :refer [check-ns]]))

(deftest check-tc
  (time (binding [*assert* false]
    (is (check-ns '[clojure.core.typed.coerce-utils
                    typed.cljc.checker.utils
                    typed.cljc.checker.type-rep
                    typed.cljc.checker.cs-rep
                    typed.cljc.checker.name-env
                    clojure.core.typed.util-vars
                    ;typed.cljc.checker.type-ctors
                    typed.cljc.dir]
                  
                  #_{:max-parallelism 4})))))

#_ ;;WIP
(deftest analyzer-test
  (is (check-ns4 'typed.cljc.analyzer)))

#_ ;;WIP
(deftest api-test
  (is (check-ns4 'typed.clojure)))

(deftest self-test-annotations
  (is (t/envs)))


(comment
  (user/debugging-tools)
  (prof/serve-ui 8085)

  (def transforms
    [;; Hide JIT compialtion from profile
     {:type :remove
      :what ";CompileBroker::compiler_thread_loop;"}
     ;; Collapse recursive check-expr
     {:type :replace
      :what #";(typed\.clj\.checker\.check/check-expr;).*\1"
      :replacement ";$1"}
     ;; Extract read+string into separate tree
     {:type :replace
      :what #".+(clojure\.tools\.reader/read\+string;)"
      :replacement ";$1"}
     ;; Extract run-pre-passes into separate tree
     {:type :replace
      :what #".+(typed\.cljc\.analyzer/run-pre-passes;)"
      :replacement ";$1"}
     ;; Extract run-post-passes into separate tree
     {:type :replace
      :what #".+(typed\.cljc\.analyzer/run-post-passes;)"
      :replacement ";$1"}
     ;; Extract analyze-outer into separate tree
     {:type :replace
      :what #".+(typed\.cljc\.analyzer/analyze-outer;)"
      :replacement ";$1"}
     ;; Collapse recursive subtypeA*
     {:type :replace
      :what #";(typed\.clj\.checker\.subtype/subtypeA\*;).*\1"
      :replacement ";$1"}
     ;; Extract subtypeA* into separate tree
     {:type :replace
      :what #".+(typed\.clj\.checker\.subtype/subtypeA\*;)"
      :replacement ";$1"}
     ;; Collapse recursive RClass-supers*
     {:type :replace
      :what #";(typed\.cljc\.checker\.type-ctors/RClass-supers\*;).*\1"
      :replacement ";$1"}
     ;; Extract force-type into separate tree
     {:type :replace
      :what #".+(typed\.cljc\.runtime\.env-utils/force-type;)"
      :replacement ";$1"}])
  (prof/set-default-profiling-options {:predefined-transforms transforms})

  (prof/generate-diffgraph 2 3 {})
  (time+ 5000 (with-out-str (check-tc)))
  (prof/profile {:title "Parallel"} (time+ 10000 (with-out-str (check-tc))))
  (prof/profile {:event :alloc}
    (time+ 10000 (with-out-str (check-tc))))

  typed.cljc.checker.cs-gen/subtype?

  (prof/generate-diffgraph 1 2 {})
  typed.cljc.analyzer.utils/source-info

  typed.clj.analyzer/unanalyzed

  typed.cljc.analyzer/run-pre-passes

  typed.cljc.analyzer.utils/merge'

  typed.cljc.analyzer.ast/children

  typed.clj.analyzer/unanalyzed

  typed.cljc.analyzer.passes.elide-meta/elide-meta

  typed.cljc.analyzer/-parse

  typed.clj.analyzer.passes.validate/validate

  typed.clj.analyzer.passes.infer-tag/infer-tag

  typed.cljc.analyzer/analyze-symbol

  typed.cljc.analyzer/analyze-let

  typed.clj.analyzer.passes.infer-tag/infer-tag

  typed.clj.analyzer.passes.annotate-tag/annotate-tag

  typed.clj.analyzer.passes.analyze-host-expr/analyze-host-expr

  typed.cljc.analyzer.utils/dissoc-env

  typed.cljc.checker.type-ctors/Datatype-ancestors

  typed.cljc.checker.type-ctors/RClass-supers*

  typed.clj.checker.parse-unparse/alias-in-ns

  typed.clj.checker.parse-unparse/clj-primitives-fn

  typed.cljc.checker.type-ctors/fully-resolve-type

  typed.cljc.checker.type-ctors/make-simple-substitution

  typed.clj.ext.clojure.core--let

  typed.cljc.runtime.env-utils/delay-type**

  typed.cljc.checker.update/env+

  typed.cljc.checker.check.cache/with-recorded-deps

  clojure.core.typed.contract-utils/hash-c?

  typed.clj.checker.parse-unparse/parse-fn-intersection-type

  typed.cljc.checker.cs-gen/cset-meet*

  typed.cljc.analyzer.utils/source-info-into-transient!

  typed.cljc.analyzer.passes.source-info/source-info

  typed.cljc.analyzer.passes.elide-meta/elide-meta

  typed.clj.analyzer.passes.annotate-host-info/annotate-host-info

  typed.cljc.analyzer.passes.uniquify/uniquify-locals

  typed.clj.analyzer.passes.analyze-host-expr/analyze-host-expr

  typed.cljc.analyzer/analyze-symbol

  typed.cljc.analyzer/propagate-top-level

  typed.clj.analyzer.passes.annotate-tag/annotate-tag

  typed.clj.analyzer.utils/try-best-match

  typed.cljc.checker.type-ctors/Datatype-ancestors

  typed.clj.checker.subtype/has-kind?

  typed.cljc.checker.name-env/resolve-name*

  typed.cljc.checker.type-ctors/resolve-Name

  typed.cljc.checker.type-ctors/fresh-symbol

  typed.cljc.checker.type-ctors/instantiate-many

  typed.cljc.checker.type-ctors/TypeFn-bbnds*

  typed.cljc.checker.type-ctors/instantiate-typefn

  typed.cljc.checker.type-ctors/TypeFn-body*

  typed.cljc.checker.tvar-env/extend-many

  typed.cljc.checker.type-ctors/TypeFn-body*

  typed.clj.checker.subtype/subtype-RClass
  )
