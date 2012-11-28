(ns combat.graph
  (:require [combat.views.common :as views]
            [clojurewerkz.neocons.rest :as nr]
            [clojurewerkz.neocons.rest.nodes :as nn]
            [clojurewerkz.neocons.rest.relationships :as nrl]
            [clojurewerkz.neocons.rest.cypher :as cy]))

;(nr/connect! "http://localhost:7474/db/data")

;;(let [node (nn/create {:name "guard"})]
;;  (println node))

(defn add-transition [node1 node2 difficulty trigger]
  (nrl/create node1 node2 :transition {:trigger trigger}))

(defn add-node [{node-name :name difficulty :difficulty parent :parent trigger :trigger :as node-properties}]
  (let [new-node (nn/create {:name node-name})]
    (when parent (add-transition parent new-node difficulty trigger))))

(defn populate []
  (let [root (nn/get 54)]
;;    (add-node {:name "Triangle" :difficulty 0.5 :parent root :trigger "if they pull their arm out"})
    (add-node {:name "Pendulum sweep"
               :alt-names ["Flower sweep"]
               :difficulty 0.5
               :parent root
               :trigger "if they stack"})
    (add-node {:name "Take the back" :difficulty 0.7 :parent root :trigger "if they push their shoulder through"})
    (add-node {:name "Rear naked choke"
           :difficulty 0.5
           :trigger "default"
           :parent (nn/get 129)})
    ))

(def fake-data
  {"details_html"
   "<h2>High Guard</h2>\n<p class='summary'>\n<ul><li><b>id:</b> 54</li><li><b>name:</b> High Guard</li><li><b>description:</b> Lock the shoulder</li></ul></p>\n"
   "data"
   {"attributes"
    [{"values" [{"name" "Guard" "id" "73"}]
      "name" "In:"
      "id" " transition"}
     {"values"
      [{"difficulty" 0.5 "name" "Arm bar" "id" "60"}
       {"name" "Triangle!!!"
        "id" "74"}
       {"name" "Pendulum sweep"
        "id" "75"}
       {"name" "Take the back"
        "id" "76"}]
      "name" "Out"
      "id" " transition"}]
    "name" "High Guard"
    "id" "54"
    }})

(defn id-from-url [url]
  (let [[_ id] (re-find #".*/(.*)" url)] id))

(defn attributize [dir {id :id {trigger :trigger} :data :as relationship}]
  {:name (or trigger "default")
   :id (id-from-url (dir relationship))})

(defn get-resource [id]
  (when-let [{id :id {name :name} :data :as node} (nn/get id)]
    {:details_html "hello"
;;    {:details_html (views/node-details node)
     :data {:name name
            :id id
            :attributes
            [{:name "In: "
              :id " transition"
              :values (map (partial attributize :start)
                           (nrl/incoming-for node))}
             {:name "Out: "
              :id " transition"
              :values (map (partial attributize :end)
                           (nrl/outgoing-for node))}]
     }}))
