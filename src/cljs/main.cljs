(ns cljs.main
  (:require [clojure.browser.repl :as repl])
  (:use [jayq.core :only [$ delegate append hide show]])
  (:use-macros [crate.def-macros :only [defpartial]]))

(def $body ($ :body))

(defn log [& items]
  (doseq [item items]
    (.log js/console item)))

(defn development? []
  (= document/domain "localhost"))
(def host (str "http://" document/domain))

(log "hello!!")
