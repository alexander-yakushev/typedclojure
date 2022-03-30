;;   Copyright (c) Ambrose Bonnaire-Sergeant, Rich Hickey & contributors.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

;; TODO clojurescript automatically rewrites clojure.core vars/macros to cljs.core
(ns ^:no-doc typed.clj.ext.clojure.core
  "Typing rules for base Clojure distribution."
  (:require [typed.cljc.checker.check.unanalyzed :as un-cljc]
            [typed.clj.checker.check.unanalyzed :as un-clj]))

;;==================
;; clojure.core/defmacro

(un-clj/install-defuspecial
  'clojure.core/defmacro
  'typed.clj.ext.clojure.core__defmacro/defuspecial__defmacro)

;;==================
;; clojure.core/defmethod

(un-clj/install-defuspecial
  'clojure.core/defmethod
  'typed.clj.ext.clojure.core__defmethod/defuspecial__defmethod)

;;==================
;; clojure.core/defn

(un-clj/install-defuspecial
  'clojure.core/defn
  'typed.clj.ext.clojure.core__defn/defuspecial__defn)

;;==================
;; clojure.core/defprotocol

(un-clj/install-defuspecial
  'clojure.core/defprotocol
  'typed.clj.ext.clojure.core__defprotocol/defuspecial__defprotocol)

;;==================
;; clojure.core/doseq

(un-clj/install-defuspecial
  'clojure.core/doseq
  'typed.clj.ext.clojure.core__doseq/defuspecial__doseq)

;; ============================
;; clojure.core/fn

(un-cljc/install-defuspecial
  #{:clojure :cljs}
  'clojure.core/fn
  'typed.clj.ext.clojure.core__fn/defuspecial__fn)

;;==================
;; clojure.core/for

(un-cljc/install-defuspecial
  #{:clojure :cljs}
  'clojure.core/for
  'typed.clj.ext.clojure.core__for/defuspecial__for)

;;==================
;; clojure.core/let

(un-clj/install-defuspecial
  'clojure.core/let
  'typed.clj.ext.clojure.core__let/defuspecial__let)

;;==================
;; clojure.core/ns

(un-clj/install-unanalyzed-special
  'clojure.core/ns
  'typed.clj.ext.clojure.core__ns/-unanalyzed-special__ns)
