(ns combat.users
  (:require [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])
            [clojurewerkz.neocons.rest :as nr]
            [clojurewerkz.neocons.rest.nodes :as nn]
            [clojurewerkz.neocons.rest.relationships :as nrl]
            [clojurewerkz.neocons.rest.cypher :as cy])
  (:import org.mindrot.jbcrypt.BCrypt))

(derive ::admin ::user)

(defn add-user- [{:keys [username password roles] :as user-properties}]
  (let [user (nn/create user-properties)]
    (nn/add-to-index user "by-username" :username username true)))

(defn add-user  username password
  {:username username
   :password (creds/hash-bcrypt password)
   :roles #{:user}})

(defn keywordize [x]
  (map keyword x))

(defn get-user [username]
  (if-let [user (:data (nn/find-one "by-username" :username username))]
    (update-in user [:roles] (comp set keywordize))))
