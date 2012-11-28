(ns cljs.main
  (:refer-clojure :exclude [find val])
  (:require [clojure.browser.repl :as repl]
            [cljs.reader :as reader]
            [fetch.remotes :as remotes]
            [clojure.string :as string]
            [cljs.reader :as reader]
            [crate.form :as form])
  (:use [jayq.core :only [$ delegate append hide show inner bind on val
                          text
                          ]]
        [jayq.util :only [log clj->js]])
  (:use-macros [crate.def-macros :only [defpartial]]
               [fetch.macros :only [letrem]]
               [combat.macros :only [when-not-empty]]
               ))

(defrecord Node
    [id location-uri data relationships-uri create-relationship-uri])
(reader/register-tag-parser! "clojurewerkz.neocons.rest.records.Node"
                             map->Node)

(def processing js/p)
(def $body ($ :body))
(def $details ($ "#explanation"))

(def init-data (reader/read-string (text ($ :#init-data))))
(def location (goog.Uri. (.-location js/window)))
(def initial-node (or (.get (.getQueryData location) "id")
                      (:initial-node init-data)))

(def origin (.-origin (.-location js/window)))

(def nodes (atom {}))

(def node-properties
  (sorted-map-by :order
                 :name {:order 1}
                 :description {:order 2
                               :type "textarea"}
                 :youtube-id {:order 3
                              :label "youtube"}))

(def new-node-properties [:new-position-name :new-trigger-name])
(def loop-properties [:looping-trigger-name :looping-node-id])

(defn horizontal-input [{:keys [id type label options] :as props}]
  [:div.control-group
   [:label.control-label {:for id} (or label id)]
   [:div.controls
    (cond (= type "textarea")
      [:textarea props (:value props)]
          (= type "drop-down")
          (form/drop-down id options)
          :default
          [:input props])]])

(defn horizontal-button [id text]
  [:div.control-group
   [:div.controls
    [:button.btn {:id id :type "submit"} text]]])

(defn property-edit [property options value]
  (let [str-prop (name property)]
    (horizontal-input (merge {:id str-prop
                              :type "text"
                              :value value}
                             options))))

(defn format-text [text]
  [:p (interpose [:br] (string/split-lines text))])

(defn get-properties [property-list]
  (into {} (map #(vector % (val ($ (str "#" (name %))))) property-list)))

(defn nodes-as-options [nodes & {exclude-id :exclude}]
  (map (fn [[id {name :name}]] [name id]) (dissoc nodes exclude-id)))

(defpartial edit-node [id {name :name :as node}]
            [:div#edit.tab-pane
             [:form.form-horizontal
              [:input {:type "hidden"
                       :id "id"
                       :value id}]
              (map (fn [[k v]]
                     (property-edit k v (k node)))
                   (reverse node-properties))
              (horizontal-button "save-btn" "Save")]
             [:h4 "Add a new position from " name]
             [:form.form-horizontal
              (horizontal-input {:id "new-trigger-name"
                                 :label "trigger"
                                 :type "text"
                                 :placeholder "if they stack"})
              (horizontal-input {:id "new-position-name"
                                 :label "name"
                                 :type "text"
                                 :placeholder "Pendulum Sweep"
                                 })
              (horizontal-button "add-btn" "Add")]
             [:h4 "Loop back to an existing position"]
             [:form.form-horizontal
              (horizontal-input {:id "looping-trigger-name"
                                 :label "trigger"
                                 :type "text"
                                 :placeholder "if they stack"})
              (horizontal-input {:id "looping-node-id"
                                 :label "position"
                                 :type "drop-down"
                                 :options (nodes-as-options @nodes :exclude id)})
              (horizontal-button "add-loop-btn" "Add")]
             [:h4 "Delete " name]
             [:form.form-horizontal
              (horizontal-button "delete-btn" "Delete")]])

(defn node-link [id]
  (str origin "/?id=" id))

(defpartial show-node [id {:keys [name
                                  description
                                  youtube-id
                                  difficulty] :as node}]
            [:div#show.tab-pane
             [:h1 name]
             [:label.permalink-label [:small "link"]]
             [:input.permalink.input-xlarge
              {:type "text"
               :value (node-link id)
               :readonly true
               :onlick "$(this).focus();$(this).select();"
               }]
             (when-not-empty difficulty
                             [:div [:strong "Difficulty:"] difficulty])
             (when-not-empty description
                             [:div.description
                              (format-text description)])
             (when-not-empty youtube-id
                             [:iframe
                              {:width 350
                               :height 262
                               :frameborder 0
                               :src (str "http://www.youtube.com/embed/" youtube-id)
                               }])])

(defpartial node-details [id node]
            [:div.details
             [:ul#nav.nav.nav-tabs
              [:li [:a {:href "#show" :data-toggle "tab"} "Info"]]
              [:li [:a {:href "#edit" :data-toggle "tab"} "Edit"]]]
             [:div.tab-content
              (show-node id node)
              (edit-node id node)]])

(defn update-details [id node]
  (inner ($ :#explanation) (node-details id node))
  (.tab ($ "#nav a:first") "show"))

(defn ^:export set-details [node-serialized]
  (let [{data :data id :id :as node} (reader/read-string node-serialized)]
    (when-not (@nodes id) (swap! nodes assoc id data))
    (update-details id (@nodes id))))

(defn process-youtube-id [url?]
 (let [uri (goog.Uri. url?)
       possible-youtube-id (.getParameterValue uri "v")]
   (or possible-youtube-id url?)))

(defn process-properties [props]
  (update-in props [:youtube-id] process-youtube-id))

(defn make-node-edit-handler [f]
  (fn [e]
      (.preventDefault e)
      (let [id (int (val ($ "#id")))]
        (f e id))))

(on $body "click" "#save-btn" nil
    (make-node-edit-handler
      (fn [e id]
        (let [props (process-properties (get-properties
                                          (keys node-properties)))]
          (letrem [result (save-node id props)]
                  (swap! nodes assoc id result)
                  (update-details id result))))))

(on $body "click" "#add-btn" nil
    (make-node-edit-handler
      (fn [e id]
        (let [{:keys [new-position-name new-trigger-name] :as props}
              (get-properties new-node-properties)]
          (when (and (not-empty new-position-name)
                     (not-empty new-trigger-name))
            (letrem [{new-id :id :as new-node}
                     (add-node-with-transition id props)]
                    (.addNodeFromCljs processing new-id)))))))

(on $body "click" "#add-loop-btn"
    (make-node-edit-handler
      (fn [e id]
        (let [{:keys [looping-node-id looping-trigger-name] :as props}
              (get-properties loop-properties)]
          (when (and (not-empty looping-node-id)
                     (not-empty looping-trigger-name))
            (letrem [results
                     (add-transition id looping-node-id looping-trigger-name)]
                    (log results)
                    (.addNodeFromCljs processing looping-node-id)
                    ))))))

(on $body "click" "#delete-btn" nil
    (make-node-edit-handler
      (fn [e id]
        (if (.confirm js/window "Are you sure? There is no undo.")
          (letrem [removed? (remove-node id)]
                  (when removed?
                    (.removeNodeFromCljs processing id)))))))

