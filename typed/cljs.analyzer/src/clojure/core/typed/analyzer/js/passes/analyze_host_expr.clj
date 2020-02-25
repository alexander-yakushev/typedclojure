;;   Copyright (c) Nicola Mometto, Rich Hickey & contributors.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

;copied from tools.analyzer.js
(ns clojure.core.typed.analyzer.js.passes.analyze-host-expr
  (:require [clojure.core.typed.analyzer.common :as common]
            [clojure.core.typed.analyzer.common.env :as env]
            [clojure.core.typed.analyzer.common.js :as anajs]))

(defmulti analyze-host-expr
  "Transform :host-interop nodes into :host-call, transform
  :maybe-class or :maybe-host-form nodes resolvable to js vars
  into :js-var nodes"
  {:pass-info {:walk :any :depends #{}}}
  :op)

(defmethod analyze-host-expr :default [ast] ast)

(defmethod analyze-host-expr :host-interop
  [{:keys [m-or-f target] :as ast}]
  (merge (dissoc ast :m-or-f)
         {:op       :host-call
          ::common/op ::ana-js/host-call
          :method   m-or-f
          :args     []
          :children [:target :args]}))

(defmethod analyze-host-expr :maybe-class
  [{:keys [class env] :as ast}]
  (if-let [v (common/resolve-sym class env)]
    (merge (dissoc ast :class)
           {:op          :js-var
            ::common/op  ::ana-js/js-var
            :var         v
            :assignable? true})
    ast))

(defmethod analyze-host-expr :maybe-host-form
  [{:keys [class field env form] :as ast}]
  (cond
   (= 'js class)
   (merge (dissoc ast :field :class)
          {:op          :js-var
           ::common/op  ::ana-js/js-var
           :var         {:op   :js-var
                         ::common/op  ::ana-js/js-var
                         :name field
                         :ns   nil}
           :assignable? true})

   (get-in (env/deref-env) [:namespaces (common/resolve-ns class env) :js-namespace])
   (let [field (or (:name (common/resolve-sym form env)) field)]
     (merge (dissoc ast :field :class)
            {:op          :js-var
             ::common/op  ::ana-js/js-var
             :var         {:op   :js-var
                           ::common/op  ::ana-js/js-var
                           :name field
                           :ns   (common/resolve-ns class env)}
             :assignable? true}))
   :else
   ast))
