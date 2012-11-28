(ns combat.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css
                            include-js
                            html5]]))

(defpartial layout [& [node-id content]]
            (html5
              [:head
               (include-css "/css/global.css")
               (include-js
                 "http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"
                 "http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.16/jquery-ui.min.js"
                 "/js/ui.js"
                 "/js/processing.min.js"
                 "/js/bin-debug/main.js")
               [:title "Combat map"]]
              [:body
               [:canvas#neoviz]
               [:header
                [:section#title
                 [:h1 "Jiu Jitsu"]
                 [:p "Position map"]]
                [:section#search
                 [:form {:action "/" :method "get"}
                  [:label "Node ID:"]
                  [:input#neoid {:type "text"
                                 :value node-id
                                 :name "neoid"}]
                  [:input#loadnode {:type "submit"
                                    :value "Load"
                                    :class "button"}]]]
                [:br {:style "clear: both;"}]
                ]
               [:section#content
                content
                (include-js "/js/init.js")]
               [:aside
                [:div#explanation]
                [:div#browser_not_supported
                 "Your Browser is currently not supported."]]
               ]))

(defpartial node-details [{id :id {name :name} :data :as node}]
            [:div.node-details
             [:h2 name]])


