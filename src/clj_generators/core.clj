(ns clj-generators.core
  (:require [clojure.core.async :as a])
  (:use criterium.core))

(deftype ItemWrapper
  [item])

(def ^:dynamic generator-chan)
(defmacro yield
  [item]
  `(do
     (a/>! generator-chan (ItemWrapper. ~item))
     (a/<! generator-chan)
     nil))

(defmacro generator
  [& body]
  `(let [c# (a/chan)]
     (binding [generator-chan c#]
       (a/go
         (a/<! generator-chan)
         ~@body
         (a/close! generator-chan))
       ((fn f# []
          (lazy-seq
            (a/>!! c# :next-item)
            (let [val# (a/<!! c#)]
              (if val#
                (cons (.item ^ItemWrapper val#) (f#))
                ()))))))))

(defmacro yield2
  [item]
  `(do
     (a/>! generator-chan ~item)))

(defmacro generator2
  [& body]
  `(let [c# (a/chan)]
     (binding [generator-chan c#]
       (a/go
         ~@body
         (a/close! generator-chan))
       ((fn f# []
          (lazy-seq
            (let [val# (a/<!! c#)]
              (if val#
                (cons val# (f#))
                ()))))))))

(defn fib-gen []
  (generator
    (let [