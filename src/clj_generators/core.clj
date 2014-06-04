(ns clj-generators.core
  (:require [clojure.core.async :as a]))

(deftype GeneratorWrapper
  [item])

(def ^:dynamic gen-chan*)

(defmacro yield
  "Must be called inside a generator. Returns the desired value as the next item in the lazy sequence,
then pauses the generator until the user requests the next value."
  ([item]
    `(do
       (a/>! gen-chan* (GeneratorWrapper. ~item))
       (a/<! gen-chan*))) ; wait until user gives permission to continue
  ([item & more]
    `(doseq [x# (list ~@(cons item more))]
       (yield x#))))

(defmacro generator
  "Takes an executable body with \"yield\" statements in it, and returns a sequence that lazily returns
all values \"yield\"ed within the body. The body will pause after each yield, until the next lazy value
is forced."
  [& body]
  `(let [c# (a/chan)]
     (binding [gen-chan* c#]
       (a/go
         (a/<! gen-chan*) ; wait until user gives permission to begin
         ~@body
         (a/close! gen-chan*))
       ((fn f# []
          (lazy-seq
            (a/>!! c# true) ; give generator permission to begin/continue
            (let [val# (a/<!! c#)]
              (if val#
                (cons (.item ^GeneratorWrapper val#) (f#))
                ()))))))))