;;   Copyright (c) Ambrose Bonnaire-Sergeant, Rich Hickey & contributors.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (https://opensource.org/license/epl-1-0/)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns typed.clj.annotator.insert
  "Utilities to insert (spec or core.typed) annotations into an
  existing file on the JVM."
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.core.typed.coerce-utils :as coerce]
            [clojure.tools.reader.reader-types :as rdrt]
            [clojure.tools.namespace.parse :as nprs]
            [typed.clj.analyzer :refer [default-opts]]
            [typed.clj.annotator.pprint :refer [pprint pprint-str-no-line]] 
            [typed.clj.annotator.util :refer [unparse-type
                                                       qualify-typed-symbol
                                                       qualify-core-symbol
                                                       *ann-for-ns*]]
            [clojure.pprint :as pp]
            [clojure.core.typed.current-impl :as impl]
            ))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Inserting/deleting annotations
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; adapted from tools.namespace
(defn update-file
  "Reads file as a string, calls f on the string plus any args, then
  writes out return value of f as the new contents of file, or writes
  content to `out`."
  [file ^String out f & args]
  {:pre [(instance? java.net.URL file)
         ((some-fn nil? string?) out)]}
  (let [old (slurp file)
        new (str (apply f old args))
        _ (when out
            (let [leading-slash? (boolean (#{\/} (first out)))
                  dirs (apply str (interpose "/" (pop (str/split out #"/"))))
                  dirs (if leading-slash?
                         (str "/" dirs)
                         dirs)
                  ;_ (prn "creating" dirs)
                  _ (doto (java.io.File. ^String dirs)
                      .mkdirs)]
              out))
        out (or out file)]
    (spit out new)
    (println "Output annotations to " out)))

(defn ns-file-name [sym]
  (io/resource
    (coerce/ns->file sym (default-opts))))

(def generate-ann-start ";; Start: Generated by clojure.core.typed - DO NOT EDIT")
(def generate-ann-end ";; End: Generated by clojure.core.typed - DO NOT EDIT")

(defn delete-generated-annotations-in-str 
  "Delete lines between generate-ann-start and generate-ann-end."
  [old]
  {:pre [(string? old)]
   :post [(string? %)]}
  (with-open [rdr (java.io.BufferedReader.
                    (java.io.StringReader. old))]
    (loop [current-open false
           lines (line-seq rdr)
           out []]
      (if (seq lines)
        (if current-open
          (if (= (first lines)
                 generate-ann-end)
            (recur false
                   (next lines)
                   out)
            (recur current-open
                   (next lines)
                   out))
          (if (= (first lines)
                 generate-ann-start)
            (recur true
                   (next lines)
                   out)
            (recur current-open
                   (next lines)
                   (conj out (first lines)))))
        (str/join "\n" out)))))

(defn ns-end-line 
  "Returns the last line of the ns form."
  [s]
  {:pre [(string? s)]
   :post [(integer? %)]}
  (let [ns-form (with-open [pbr (rdrt/indexing-push-back-reader
                                  (rdrt/string-push-back-reader s))]
                  (nprs/read-ns-decl pbr nprs/clj-read-opts))
        _ (assert ns-form "No namespace form found")
        end-line (-> ns-form meta :end-line)
        _ (assert (integer? end-line) 
                  (str "No end-line found for ns form"
                       (meta ns-form)))]
    end-line))

(def ^:dynamic *indentation* 2)

(defn split-at-column 
  ([s column] (split-at-column s column nil))
  ([s column end-column]
   (let [before (subs s 0 (dec column))
         after  (if end-column
                  (subs s (dec column) (dec end-column))
                  (subs s (dec column)))]
     [before after])))

;; returns a pair [leading-first-line file-slice trailing-final-line]
(defn extract-file-slice [ls line column end-line end-column]
  (let [;_ (prn "ls" (count ls) (dec line) end-line)
        v (subvec ls (dec line) end-line)
        first-line (nth v 0)
        last-line (peek v)
        ;_ (prn "last-line" last-line (dec end-column))
        [before-column after-column] (split-at-column first-line column 
                                                      (when (= line end-line)
                                                        end-column))
        [before-end-column after-end-column] (split-at-column last-line end-column)
        ]
    [before-column
     (if (= 1 (count v))
       (assoc v
              0 after-column)
       (assoc v
              0 after-column
              (dec (count v)) before-end-column))
     after-end-column]))

(defn restitch-ls [ls line end-line split]
  (vec (concat
         (subvec ls 0 (dec line))
         split
         (subvec ls end-line))))

(defn insert-loop-var [{:keys [line column end-line end-column] :as f} ls]
  {:pre [(#{:loop-var} (:typed.clj.annotator.track/track-kind f))
         #_(= line end-line)
         #_(< column end-column)
         ]}
  (let [end-line line
        end-column column
        [leading file-slice trailing] (extract-file-slice ls line column end-line end-column)
        ;_ (prn "leading" leading) 
        ;_ (prn "file-slice" file-slice) 
        ;_ (prn "trailing" trailing)
        the-ann (binding [*print-length* nil
                          *print-level* nil]
                  (with-out-str 
                    ;(print "^")
                    ;(print (pprint-str-no-line :clojure.core.typed/rt-gen))
                    ;(print " ")
                    (print "^{")
                    (print (pprint-str-no-line :clojure.core.typed/ann))
                    (print " ")
                    (print (pprint-str-no-line (unparse-type (:type f))))
                    (print "} ")))
        [full-first-line
         offset-first-line]
        (if (> (count leading) 0)
          (let [extra-columns (atom 0)
                last-char (nth leading (dec (count leading)))]
            [(str leading 
                  (when-not (#{\[ \space} last-char)
                    (swap! extra-columns inc)
                    " ")
                  the-ann)
             (+ @extra-columns (count the-ann))])
          [the-ann (count the-ann)])
        ; FIXME this should always be [""], but it adds a useless new line
        ;file-slice
        _ (assert (every? #{""} file-slice)
                  file-slice)
        final-split [(str full-first-line trailing)]
        new-ls (restitch-ls ls line end-line final-split)
        update-line (fn [old-line]
                      ;; we never add a new line
                      old-line)
        update-column (fn [old-column old-line]
                        (cond
                          ;; changes in the current line. Compensate
                          ;; for the type annotation.
                          (and (= old-line line)
                               (< column old-column))
                          (+ old-column offset-first-line)
                          ;; we preserve columns since we don't add
                          ;; extra indentation.
                          :else old-column))]
    {:ls new-ls
     :update-line update-line
     :update-column update-column}))


(defn insert-local-fn* [{:keys [line column end-line end-column] :as f} ls]
  {:pre [(#{:local-fn} (:typed.clj.annotator.track/track-kind f))]}
  (let [;_ (prn "current fn" f)
        [before-first-pos file-slice trailing] (extract-file-slice ls line column end-line end-column)
        ;_ (prn "before-first-pos" before-first-pos) 
        ;_ (prn "file-slice" file-slice) 
        ;_ (prn "trailing" trailing)
        after-first-pos (nth file-slice 0)
        ;_ (prn "after-first-pos" after-first-pos)
        before-line (str
                      before-first-pos
                      (binding [*print-length* nil
                                *print-level* nil]
                        (with-out-str 
                          ;; DON'T DELETE THESE PRINTS
                          (print "(")
                          ;(print (str "^" (pprint-str-no-line :clojure.core.typed/auto-gen) " "))
                          (print (pprint-str-no-line (qualify-typed-symbol 'ann-form))))))
        indentation *indentation*
        indentation-spaces (apply str (repeat (+ (dec column) indentation) " "))
        ;; insert column+indentation spaces
        the-fn-line (str indentation-spaces after-first-pos)

        rest-slice (if (= 1 (count file-slice))
                     []
                     (subvec file-slice 1 (count file-slice)))

        ;; indent each line at column
        indented-fn (map (fn [a]
                           {:pre [(string? a)]}
                           ;; insert indentation at column if there's already whitespace there
                           (if (= \space (nth a (dec column)))
                             (let [;_ (prn "indenting" a)
                                   ;_ (prn "left half " (subs a 0 (dec column)))
                                   ;_ (prn "right half" (subs a (dec column)))
                                   ]
                               (str (subs a 0 column)
                                    (apply str (repeat indentation " "))
                                    (subs a column)))
                             (do
                               (prn (str
                                      "WARNING: Not indenting line " line
                                      " of " (:ns f) ", found non-whitespace "
                                      " at column " column "."))
                               a)))
                         rest-slice)
        ;_ (prn "the type pp" (pprint-str-no-line (unparse-type (:type f))))
        the-type-line (str indentation-spaces
                           (pprint-str-no-line (unparse-type (:type f)))
                           ")")
        ;; now add any trailing code after end-column
        ;; eg. (map (fn ...) c) ==> (map (ann-form (fn ...) ...)
        ;;                               c)
        trailing-line (when (not= 0 (count trailing))
                        (str (apply str (repeat (dec column) " "))
                             ;; TODO compensate for this change in update-column
                             (if nil #_(= \space (nth trailing 0))
                               (subs trailing 1)
                               trailing)))

        final-split (concat
                      [before-line
                       the-fn-line]
                      indented-fn
                      [the-type-line]
                      (when trailing-line
                        [trailing-line]))
        new-ls (restitch-ls ls line end-line final-split)
        update-line (fn [old-line]
                      (cond
                        ;; occurs before the current changes
                        (< old-line line) old-line
                        ;; occurs inside the bounds of the current function.
                        ;; Since we've added an extra line before this function (the beginning ann-form)
                        ;; we increment the line.
                        (<= line old-line end-line) (inc old-line)
                        ;; occurs after the current function.
                        ;; We've added possibly 2-3 lines: 
                        ;; - the beginning of the ann-form
                        ;; - the end of the ann-form
                        ;; - possibly, the trailing code
                        :else (if trailing-line
                                (+ 3 old-line)
                                (+ 2 old-line))))
        update-column (fn [old-column old-line]
                        (cond
                          ;; occurs before the current changes
                          (< old-line line) old-column
                          ;; occurs inside the bounds of the current function.
                          ;; We indent each of these lines by 2.
                          ;; WARNING: we might not have indented here
                          (<= line old-line end-line) (+ 2 old-column)
                          :else old-column))]
  {:ls new-ls
   :update-line update-line
   :update-column update-column}))

(defn insert-local-fns [local-fns old config]
  {:post [(string? %)]}
  ;(prn "insert-local-fns" local-fns)
  (let [update-coords
        (fn [update-line update-column]
          ;; adjust the coordinates of any functions that have moved.
          (fn [v]
            (-> v
                (update :line update-line)
                (update :end-line update-line)
                ;; pass original line
                (update :column update-column (:line v))
                ;; pass original end-line
                (update :end-column update-column (:end-line v)))))
        ;; reverse
        sorted-fns (sort-by (juxt :line :column) local-fns)
        ls (with-open [pbr (java.io.BufferedReader.
                             (java.io.StringReader. old))]
             (vec (doall (line-seq pbr))))]
    ;(prn "top ls" (count ls))
    (loop [ls ls
           fns sorted-fns]
      ;(prn "current ls")
      ;(println (str/join "\n" ls))
      (if (empty? fns)
        (str/join "\n" ls)
        (let [;; assume these coordinates are correct
              f (first fns)
              ;_ (prn "current f" f)
              {:keys [ls update-line update-column]}
              (case (:typed.clj.annotator.track/track-kind f)
                :local-fn (insert-local-fn* f ls)
                :loop-var (insert-loop-var f ls))
              _ (assert (vector? ls))
              _ (assert (fn? update-line))
              _ (assert (fn? update-column))
              next-fns (map 
                         ;; adjust the coordinates of any functions that have moved.
                         (update-coords update-line update-column)
                         (next fns))]
          (recur ls
                 next-fns))))))

(comment
  (println
    (insert-local-fns
      [{:typed.clj.annotator.track/track-kind :local-fn
        :line 1 :column 1
        :end-line 1 :end-column 11
        :type {:op :Top}}]
      "(fn [a] a)"
      {}))
  (println
    (insert-local-fns
      [{:typed.clj.annotator.track/track-kind :local-fn
        :line 1 :column 1
        :end-line 2 :end-column 5
        :type {:op :Top}}]
      "(fn [a]\n  a) foo"
      {}))
  (println
    (insert-local-fns
      [{:typed.clj.annotator.track/track-kind :local-fn
        :line 1 :column 3
        :end-line 2 :end-column 7
        :type {:op :Top}}]
      "  (fn [a]\n    a) foo"
      {}))
  (println
    (insert-local-fns
      [{:typed.clj.annotator.track/track-kind :local-fn
        :line 1 :column 1
        :end-line 1 :end-column 20
        :type {:op :Top}}
       {:typed.clj.annotator.track/track-kind :local-fn
        :line 1 :column 9
        :end-line 1 :end-column 19
        :type {:op :Top}}]
      "(fn [b] (fn [a] a))"
      {}))
  )

(declare prepare-ann)

(defn insert-generated-annotations-in-str
  "Insert annotations after ns form."
  [old ns {:keys [replace-top-level? no-local-ann? infer-anns] :as config}]
  {:pre [(string? old)]
   :post [(string? %)]}
  ;(prn "insert" ann-str)
  (binding [*ns* (the-ns ns)
            *ann-for-ns* #(the-ns ns)]
    (let [{:keys [requires top-level local-fns] :as as} (infer-anns ns config)
          ann-str (prepare-ann requires top-level config)
          _ (assert (string? ann-str))
          old (if no-local-ann?
                old
                (insert-local-fns local-fns old config))
          old (delete-generated-annotations-in-str old)
          insert-after (ns-end-line old)]
      (with-open [pbr (java.io.BufferedReader.
                        (java.io.StringReader. old))]
        (loop [ls (line-seq pbr)
               current-line 0
               out []]
          (if (= current-line insert-after)
            (str/join "\n" (concat out 
                                   [(first ls)
                                    ;""
                                    ann-str]
                                   (rest ls)))
            (if (seq ls)
              (recur (next ls)
                     (inc current-line)
                     (conj out (first ls)))
              (str/join "\n" (concat out 
                                     [""
                                      ann-str])))))))))
    


(defn delete-generated-annotations [ns config]
  (update-file (ns-file-name (if (symbol? ns)
                               ns ;; avoid `the-ns` call in case ns does not exist yet.
                               (ns-name ns)))
               nil
               delete-generated-annotations-in-str))

(defn prepare-ann [requires top-level config]
  {:post [(string? %)]}
  (binding [*print-length* nil
            *print-level* nil]
    (with-out-str
      ;; print requires outside start/end annotations so we don't
      ;; delete them between runs
      (when (seq requires)
        (println ";; Automatically added requires by core.typed")
        (doseq [[n a] requires]
          (pprint (list (qualify-core-symbol 'require) `'[~n :as ~a]))))
      (println generate-ann-start)
      (doseq [a top-level]
        (pprint a))
      (print generate-ann-end))))

(defn default-out-dir [{:keys [spec?] :as config}]
  (let [cp-root (-> "" java.io.File. .getAbsoluteFile .getPath)
        dir-name (str "generated-" (if spec? "spec" "type") "-annotations")]
    (str cp-root "/" dir-name)))

(defn insert-or-replace-generated-annotations [ns {:keys [out-dir] :as config}]
  (let [nsym (ns-name ns)
        ^java.net.URL
        file-in (ns-file-name nsym)]
    (update-file file-in
                 (when (or out-dir 
                           (not= "file" (.getProtocol file-in)))
                   (str (or out-dir
                            (default-out-dir config))
                        "/" 
                        (coerce/ns->file nsym (default-opts))))
                 insert-generated-annotations-in-str
                 ns
                 config)))

(defn insert-generated-annotations [ns config]
  (insert-or-replace-generated-annotations ns config))
(defn replace-generated-annotations [ns config]
  (insert-or-replace-generated-annotations ns (assoc config :replace-top-level? true)))
