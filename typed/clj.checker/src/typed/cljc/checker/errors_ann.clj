;;   Copyright (c) Ambrose Bonnaire-Sergeant, Rich Hickey & contributors.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns typed.cljc.checker.errors-ann
  (:require [typed.clojure :as t]))

(t/ann ^:no-check clojure.core.typed.errors/deprecated-warn [t/Str -> nil])
(t/ann ^:no-check clojure.core.typed.errors/int-error [t/Str -> t/Nothing])

