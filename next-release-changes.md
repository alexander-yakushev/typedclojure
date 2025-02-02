- annotate `clojure.core/empty`
- support `(t/Val "string")` in `t/pred`
- Close https://github.com/typedclojure/typedclojure/issues/143
  - correctly scope datatype type variables when checking deftype
  - fix t/Match to work without type variables
- revert bad optimization in analyzer (caught by David Miller during CLR port)
- support Clojure 1.9.0 in analyzers
- add alternative syntax for "or" proposition: (or Props...)
- using requiring-resolve to resolve protocol vars during type checking
  - allows `:as-alias` to be used in annotations
- add clj-kondo hook for `clojure.core.typed/pred`
- add `clojure.string/starts-with?` annotation
- default `:check-config` `:check-ns-dep` to `:never` and fix `:recheck`
- add `{:typed.clojure {:experimental #{:cache}}}` namespace meta to start investigating caching of type checking results
  - currently prints results to screen
- fix verbose printing for composite types
- don't uniquify type variables with verbose printing
  - add new new `:unique-tvars` option to `*verbose-types*` to uniquify type variable names
- namespace-level functions like `check-ns` now `require`s the namespace being checked before type checking it, and does not evaluate individual forms
  - usual `require` behavior applies (only loads if not already loaded), so user is now responsible for reloading type annotations, similar to spec
    - e.g., if you change a `t/ann`, make sure to evaluate the form just as you would an `s/def`
  - this enables future optimizations to type check forms in parallel and integrate type checking with tools.namespace/clj-reload namespace loading hooks
  - for previous behavior, use:
```clojure
(check-ns *ns* :check-config {:check-ns-load :never, :check-form-eval :after})
```
