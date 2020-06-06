# Typed Clojure

<div>
  <a href="https://typedclojure.org">
    <img src="doc/images/typed-clojure-an-optional-type-system-letterbox.png"
         alt="Typed Clojure">
  </a>
</div>

<div>
  <p>
    <a href="https://www.patreon.com/ambrosebs">
      <img src="doc/images/become_a_patron_button.png"
           alt="Become a patron">
    </a>
    <a href="https://opencollective.com/typedclojure">
      <img src="doc/images/donate-to-our-collective.png"
           alt="Donate to your OpenCollective">
    </a>
  </p>
</div>

Optional typing in Clojure, as a library.

## Usage Overview

Typed Clojure is separated into modules. You'll want the full type checker at development
time, and the runtime dependencies in production.

In Clojure CLI's `deps.edn`, this will look something like this:

```clojure
{:deps {typed.clj/runtime {:mvn/version "1.0.12"}}
 :aliases {:dev {:extra-deps {typed.clj/checker {:mvn/version "1.0.12"}}}}}
```

You can then start a dev repl with `clj -A:dev`.

In Leiningen's `project.clj`, something like this:

```clojure
(defproject a-project "0.0.1-SNAPSHOT"
  :dependencies [[typed.clj/runtime "1.0.12"]]
  :profiles {:dev {:dependencies [[typed.clj/checker "1.0.12"]]}})
```

Then, `lein repl` will automatically activate the `:dev` profile. Verify the type
checker is not included in uberjars with `lein with-profile uberjar deps :tree`.

## Releases and Dependency Information

Latest stable release is 1.0.12.

See modules for specific version coordinates:

### Clojure implementation

* [typed.clj/checker](typed/clj.checker/README.md): The JVM type checker
* [typed.clj/runtime](typed/clj.runtime/README.md): JVM Runtime dependencies
* [typed.clj/analyzer](typed/clj.analyzer/README.md): Analyzer for JVM Clojure
* [typed.clj/annotator.](typed/clj.annotator/README.md): Automatic annotator
* [typed.clj/lang](typed/clj.lang/README.md): Extensible languages
* [typed.clj/spec](typed/clj.spec/README.md): Type-like specs.

### ClojureScript implementation (Work in progress)

* [typed.cljs/analyzer](typed/cljs.analyzer/README.md): Analyzer for JS Clojure (unreleased)
* [typed.cljs/checker](typed/cljs.checker/README.md): The JS type checker (unreleased)

### Implementation-agnostic

* [typed.cljc/analyzer](typed/cljc.analyzer/README.md): Implementation-agnostic base for Clojure analyzers

### Library Annotations

* [typed.lib/clojure](typed/lib.clojure/README.md): Base type annotations
* [typed.lib/core.async](typed/lib.core.async/README.md): Annotations for core.async

## Compatibility

Typed Clojure supports Clojure 1.10.1 and JDK 1.8+.

## Mailing List and Chat

Use the core.typed [mailing list](https://groups.google.com/forum/?fromgroups#!forum/clojure-core-typed) for Typed Clojure discussion.

[#core-typed on Clojurians Slack](http://clojurians.slack.com/app_redirect?channel=core-typed) (most active, for short announcements and questions)

[Archive for #core-typed Slack](https://clojurians.zulipchat.com/#narrow/stream/180378-slack-archive/topic/core-typed)

[#typedclojure on ZulipChat](https://clojurians.zulipchat.com/#narrow/stream/231516-typedclojure) (for longer discussions due to threading and unlimited history)

## Documentation

See [wiki](https://github.com/clojure/core.typed/wiki).

[API Reference](https://clojure.github.io/core.typed/)

[Ambrose's blog](https://frenchy64.github.io/)

## Leiningen Plugin

[lein-typed](https://github.com/frenchy64/lein-typed)

## Vim Plugin

[vim-typedclojure](https://github.com/typedclojure/vim-typedclojure)

## Emacs Mode

[typed-clojure-mode](https://github.com/typedclojure/typed-clojure-mode)

## Examples

[core.async Rock-paper-scissors](https://github.com/clojure/core.typed/blob/master/module-check/src/test/clojure/clojure/core/typed/test/rps_async.clj)

## Developer Information

- [Typed Clojure Contributor Code of Conduct](CODE_OF_CONDUCT.md)
- [Contributing guidelines](CONTRIBUTING.md)

<!---
## Future work

* Equality filters for occurrence typing
* Unify AST with ClojureScript
* Namespace dependency management
* Track changes to Typed Racket
  * https://github.com/plt/racket/compare/6105ce8b2087...71d6189132ce
-->

## Contributors

Thanks to the following people for contributing to core.typed:

* Stephen Compall (S11001001)
* Andrew Brehaut (brehaut)
* Christopher Spencer (cspencer)
* Reid McKenzie (arrdem)
* Di Xu (xudifsd)
* Nicola Mometto (Bronsa)
* Chas Emerick (cemerick)
* Jake Goulding (shepmaster)
* Andy Fingerhut
* Aravind K N (arav93)
* Allen Rohner (arohner)
* Minori Yamashita (ympbyc)
* Kyle Kingsbury (aphyr)
* Nathan Sorenson
* Tobias Kortkamp (t6)
* Alejandro Gomez (dialelo)
* Piotr Jarzemski (kazuhiro)

## Sponsors

Thanks to the following companies for their active sponsorship of Typed Clojure's development
via [OpenCollective](https://opencollective.com/typedclojure).

<div>
  <div>
    <a href="https://www.adgoji.com/">
      <img src="https://typedclojure.org/images/sponsors/adgoji.png" alt="Adgoji">
    </a>
  </div>
</div>

## YourKit

YourKit is kindly supporting core.typed and other open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of innovative and intelligent tools for profiling
Java and .NET applications. Take a look at YourKit's leading software products:

* <a href="https://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a> and
* <a href="https://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>.

## License

Copyright © Ambrose Bonnaire-Sergeant, Rich Hickey & contributors.

Licensed under the EPL (see the file epl-v10.html).

### tools.analyzer

Copyright © Nicola Mometto, Rich Hickey & contributors.

Licensed under the EPL (see the file epl-v10.html).

### tools.analyzer.jvm

Copyright © Nicola Mometto, Rich Hickey & contributors.

Licensed under the EPL (see the file epl-v10.html).

### tools.analyzer.js

Copyright © Nicola Mometto, Rich Hickey & contributors.

Licensed under the EPL (see the file epl-v10.html).
