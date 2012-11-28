(ns jayq.core
  (:refer-clojure :exclude [val empty remove find next parents])
  (:require [clojure.string :as string]
            [cljs.reader :as reader])
  (:use [jayq.util :only [clj->js]]))

(defn crate-meta [func]
  (.-prototype._crateGroup func))

(defn ->selector [sel]
  (cond
   (string? sel) sel
   (fn? sel) (if-let [cm (crate-meta sel)]
               (str "[crateGroup=" cm "]")
               sel)
   (keyword? sel) (name sel)
   :else sel))

(defn $
  ([sel context]
     (js/jQuery (->selector sel) context))
  ([sel]
     (js/jQuery (->selector sel))))

(extend-type js/jQuery
  ISeqable
  (-seq [this] (when (.get this 0)
                 this))
  ISeq
  (-first [this] (.get this 0))
  (-rest [this] (if (> (count this) 1)
                  (.slice this 1)
                  (list)))

  ICounted
  (-count [this] (.-length this))

  IIndexed
  (-nth [this n]
    (when (< n (count this))
      (.slice this n (inc n))))
  (-nth [this n not-found]
    (if (< n (count this))
      (.slice this n (inc n))
      (if (undefined? not-found)
        nil
        not-found)))

  ISequential

  ILookup
  (-lookup
    ([this k]
       (or (.slice this k (inc k)) nil))
    ([this k not-found]
       (-nth this k not-found)))

  IReduce
  (-reduce [this f]
    (ci-reduce this f))
  (-reduce [this f start]
    (ci-reduce this f start)))

(set! jQuery.prototype.call
      (fn
        ([_ k] (-lookup (js* "this") k))
        ([_ k not-found] (-lookup (js* "this") k not-found))))

(defn anim [elem props dur]
  (.animate elem (clj->js props) dur))

(defn text
  ([$elem]
     (.text $elem))
  ([$elem txt]
     (.text $elem txt)))

(defn css
  ([$elem p v]
     (.css $elem (name p) v))
  ([$elem opts]
     (.css $elem (clj->js opts))))

(defn attr
  ([$elem n v]
     (. $elem (attr (name n) v)))
  ([$elem x]
     (. $elem (attr (clj->js x)))))

(defn remove-attr [$elem a]
  (.removeAttr $elem (name a)))

(defn data
  ([$elem k v]
     (. $elem (data (name k) v)))
  ([$elem x]
     (. $elem (data (clj->js x)))))

(defn add-class [$elem cl]
  (.addClass $elem (name cl)))

(defn remove-class [$elem cl]
  (.removeClass $elem (name cl)))

(defn toggle-class [$elem cl]
  (.toggleClass $elem (name cl)))

(defn has-class [$elem cl]
  (.hasClass $elem (name cl)))

(defn is [$elem selector]
  (.is $elem (->selector selector)))

(defn after [$elem content]
  (.after $elem content))

(defn before [$elem content]
  (.before $elem content))

(defn append [$elem content]
  (.append $elem content))

(defn prepend [$elem content]
  (.prepend $elem content))

(defn append-to [$elem target]
  (.appendTo $elem (->selector target)))

(defn prepend-to [$elem target]
  (.prependTo $elem (->selector target)))

(defn insert-before [$elem target]
  (.insertBefore $elem (->selector target)))

(defn insert-after [$elem target]
  (.insertAfter $elem (->selector target)))

(defn remove [$elem]
  (.remove $elem))

(defn hide [$elem & [speed on-finish]]
  (.hide $elem speed on-finish))

(defn show [$elem & [speed on-finish]]
  (.show $elem speed on-finish))

(defn toggle [$elem & [speed on-finish]]
  (.toggle $elem speed on-finish))

(defn fade-out [$elem & [speed on-finish]]
  (.fadeOut $elem speed on-finish))

(defn fade-in [$elem & [speed on-finish]]
  (.fadeIn $elem speed on-finish))

(defn slide-up [$elem & [speed on-finish]]
  (.slideUp $elem speed on-finish))

(defn slide-down [$elem & [speed on-finish]]
  (.slideDown $elem speed on-finish))

(defn siblings
  ([$elem]
     (.siblings $elem))
  ([$elem selector]
     (.siblings $elem (name selector))))

(defn parent [$elem]
  (.parent $elem))

(defn parents
  ([$elem]
     (.parents $elem))
  ([$elem selector]
     (.parents $elem (name selector))))

(defn parents-until
  ([$elem]
     (.parentsUntil $elem))
  ([$elem selector]
     (.parentsUntil $elem (->selector selector)))
  ([$elem selector filtr]
     (.parentsUntil $elem (->selector selector) (name filtr))))

(defn children
  ([$elem selector]
     (.children $elem (name selector)))
  ([$elem]
     (.children $elem)))

(defn next
  ([$elem]
     (.next $elem))
  ([$elem selector]
     (.next $elem (name selector))))

(defn prev
  ([$elem]
     (.prev $elem))
  ([$elem selector]
     (.prev $elem (name selector))))

(defn next-all
  ([$elem]
     (.nextAll $elem))
  ([$elem selector]
     (.nextAll $elem (name selector))))

(defn prev-all
  ([$elem]
     (.prevAll $elem))
  ([$elem selector]
     (.prevAll $elem (name selector))))

(defn next-until
  ([$elem]
     (.nextUntil $elem))
  ([$elem selector]
     (.nextUntil $elem (->selector selector)))
  ([$elem selector filtr]
     (.nextUntil $elem (->selector selector) (name filtr))))

(defn prev-until
  ([$elem]
     (.prevUntil $elem))
  ([$elem selector]
     (.prevUntil $elem (->selector selector)))
  ([$elem selector filtr]
     (.prevUntil $elem (->selector selector) (name filtr))))

(defn find [$elem selector]
  (.find $elem (name selector)))

(defn closest [$elem selector & [context]]
  (.closest $elem (->selector selector) context))

(defn clone [$elem]
  (.clone $elem))

(defn inner [$elem v]
  (.html $elem v))

(defn empty [$elem]
  (.empty $elem))

(defn val
  ([$elem v]
     (.val $elem v))
  ([$elem]
     (.val $elem)))

(defn serialize [$elem]
  (.serialize $elem))

(defn queue [$elem callback]
  (. $elem (queue callback)))

(defn dequeue [elem]
  (. ($ elem) (dequeue)))

(defn document-ready [func]
  (.ready ($ js/document) func))

(defn xhr [[method uri] content callback]
  (let [params (clj->js {:type (string/upper-case (name method))
                         :data (clj->js content)
                         :success callback})]
    (.ajax js/jQuery uri params)))

(defn ajax
  ([url settings]
     (.ajax js/jQuery url (clj->js settings)))
  ([settings]
     (.ajax js/jQuery (clj->js settings))))

(defn ^:private mimetype-converter [s]
  (reader/read-string (str s)))

(.ajaxSetup js/jQuery
            (clj->js
             {:accepts {:edn "application/edn, text/edn"
                        :clojure "application/clojure, text/clojure"}
              :contents {"clojure" #"edn|clojure"}
              :converters
              {"text edn" mimetype-converter
               "text clojure" mimetype-converter}}))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Events
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn bind [$elem ev func]
  (.bind $elem (name ev) func))

(defn unbind [$elem ev & [func]]
  (.unbind $elem (name ev) func))

(defn trigger [$elem ev]
  (.trigger $elem (name ev)))

(defn delegate [$elem sel ev func]
  (.delegate $elem (->selector sel) (name ev) func))

(defn ->event [e]
  (if (coll? e)
    (string/join " " (map name e))
    (clj->js e)))

(defn on [$elem events & [sel data handler]]
  (.on $elem
       (->event events)
       (->selector sel)
       data
       handler))

(defn one [$elem events & [sel data handler]]
  (.one $elem
        (->event events)
        (->selector sel)
        data
        handler))

(defn off [$elem events & [sel handler]]
  (.off $elem
        (->event events)
        (->selector sel)
        handler))

(defn prevent [e]
  (.preventDefault e))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Dimensions & Offset
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn height
  ([$elem x]
     (.height $elem x))
  ([$elem]
     (.height $elem)))

(defn width
  ([$elem x]
     (.width $elem x))
  ([$elem]
     (.width $elem)))

(defn inner-height
  [$elem]
  (.innerHeight $elem))

(defn inner-width
  [$elem]
  (.innerWidth $elem))

(defn outer-height
  [$elem]
  (.outerHeight $elem))

(defn outer-width
  [$elem]
  (.outerWidth $elem))

(defn offset
  ([$elem coords]
     (.offset (clj->js coords)))
  ([$elem]
     (js->clj (.offset $elem) :keywordize-keys true)))

(defn offset-parent
  [$elem]
  (.offsetParent $elem))

(defn position [$elem]
  (js->clj (.position $elem) :keywordize-keys true))

(defn scroll-left
  ([$elem x]
     (.scrollLeft $elem x))
  ([$elem]
     (.scrollLeft $elem)))

(defn scroll-top
  ([$elem x]
     (.scrollTop $elem x))
  ([$elem]
     (.scrollTop $elem)))
