(ns combat.server
  (:require [noir.server :as server]
            [clojurewerkz.neocons.rest :as nr]
            [clojurewerkz.neocons.rest.nodes :as nn]
            [clojurewerkz.neocons.rest.relationships :as nrl]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [combat.users :as users]
            [combat.graph :as graph]
            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])))

(server/load-views-ns 'combat.views)

(server/add-middleware friend/authenticate
                       {:credential-fn
                        (partial creds/bcrypt-credential-fn users/get-user)
                        :workflows [(workflows/interactive-form)]
                        :unauthorized-handler
                        (constantly
                         {:status 401
                          :body
                          (pr-str
                            "Sorry, you do not have access to this resource.")})})

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (graph/connect!)
    (server/start port {:mode mode
                        :ns 'combat})))
