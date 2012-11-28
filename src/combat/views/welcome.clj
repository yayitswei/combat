(ns combat.views.welcome
  (:require [combat.views.common :as common]
            [noir.content.getting-started]
            [clojurewerkz.neocons.rest.nodes :as nn]
            [combat.graph :as graph]
            [hiccup.form :as form]
            [cemerick.friend :as friend]
            )
  (:use [noir.core :only [defpage]]
        [noir.response :only [json]]
        [noir.fetch.remotes]
        ))

(defpage
  "/" {:keys [id]}
  (friend/authorize
    #{:user}
    (common/layout-main id)))

(defpage
  [:get "/resources/show"] {:keys [id]}
  (json
    (graph/get-resource (read-string id))))

(defn cast-id [id]
  (read-string (str id)))

(defremote save-node [id properties]
           (println "saving " id ":" (pr-str properties))
           (graph/save-node (read-string (str id)) properties))

(defremote add-node-with-transition [parent-id {name :new-position-name
                                                trigger :new-trigger-name
                                                :as props
                                                }]
           (println "Adding node to " parent-id ": " props)
           (let [parent (nn/get (read-string (str parent-id)))]
             (graph/add-node {:name name :trigger trigger :parent parent})))

(defremote remove-node [id]
           (graph/remove-node id))

(defremote add-transition [node1-id node2-id trigger-name]
           (println "Adding transition from" node1-id " to " node2-id)
           (let [node1 (nn/get (cast-id node1-id))
                 node2 (nn/get (cast-id node2-id))]
             (graph/add-transition node1 node2 nil trigger-name)))

(defpage "/login" {username :username}
         (common/layout-login
           [:div.container.login
            (form/form-to
              {:class "form-login"} [:put "/login"]
              [:h2 "Please sign in"]
              (form/text-field
                {:class "input-block-level"
                 :placeholder "Username"
                 :value username
                 } "username")
              (form/password-field
                {:class "input-block-level"
                 :placeholder "Password"
                 } "password")
              [:br]
              (form/submit-button
                {:class "btn btn-large btn-primary"}
                "Go"))]))
