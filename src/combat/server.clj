(ns combat.server
  (:require [noir.server :as server]
            [clojurewerkz.neocons.rest :as nr]
            [clojurewerkz.neocons.rest.nodes :as nn]
            [clojurewerkz.neocons.rest.relationships :as nrl]
            [clojurewerkz.neocons.rest.cypher :as cy]))

(server/load-views-ns 'combat.views)

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (nr/connect! "http://localhost:7474/db/data")
    (server/start port {:mode mode
                        :ns 'combat})))
