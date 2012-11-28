(ns combat.graph
  (:require [combat.views.common :as views]
            [clojurewerkz.neocons.rest :as nr]
            [clojurewerkz.neocons.rest.nodes :as nn]
            [clojurewerkz.neocons.rest.relationships :as nrl]
            [clojurewerkz.neocons.rest.cypher :as cy]))

(defn connect! []
  (nr/connect! (or (System/getenv "NEO4J_URL")
                   "http://localhost:7474/db/data")
               (System/getenv "NEO4J_LOGIN")
               (System/getenv "NEO4J_PASSWORD"))
  (nn/create-index "by-username"))

(defn add-transition [node1 node2 difficulty trigger]
  (nrl/create node1 node2 :transition {:trigger trigger})
  true)

(defn add-node [{node-name :name difficulty :difficulty parent :parent trigger :trigger :as node-properties}]
  (let [new-node (nn/create {:name node-name})]
    (when parent (add-transition parent new-node difficulty trigger))
    new-node))

(defn id-from-url [url]
  (let [[_ id] (re-find #".*/(.*)" url)] id))

(defn attributize [dir {id :id {trigger :trigger} :data :as relationship}]
  {:name (or trigger "default")
   :id (id-from-url (dir relationship))})

(defn attributes [node]
  (filter identity
          [(let [incoming (nrl/incoming-for node)]
             (when-not (empty? incoming)
               {:name "In: "
                :id " transition"
                :values (map (partial attributize :start)
                             incoming)}))
           (let [outgoing (nrl/outgoing-for node)]
             (when-not (empty? outgoing)
               {:name "Out: "
                :id " transition"
                :values (map (partial attributize :end) outgoing)}))]))

(defn get-resource [id]
  (when-let [{id :id {name :name} :data :as node} (nn/get id)]
    {:details_html (views/node-details node)
     :node (pr-str node)
     :data {:name name
            :id id
            :attributes (attributes node)}}))

(defn save-node [id properties]
  (nn/update id properties))

(defn remove-node [id]
  (let [node (nn/get id)]
    (nrl/purge-all node)
    (nn/delete id)))
