(ns combat.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css
                            include-js
                            html5]]))

(def prod (System/getenv "LEIN_NO_DEV"))

(def main-js-path (if prod
                    "/js/bin/main.js"
                    "/js/bin-debug/main.js"))

(def asset-host (when prod "http://combat-app.s3.amazonaws.com"))

(defn init-data [data]
  [:script {:type "text/edn"
            :id "init-data"}
   (pr-str data)])

(defpartial layout-login [content]
            (html5
              [:head
               (include-css
                 "//netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/css/bootstrap.min.css"
                 "/css/global.css"
                 )
               [:link {:href "/less/styles.less",
                       :type "text/css",
                       :rel "stylesheet/less"}]
               (include-js
                 "http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"
                 "http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.16/jquery-ui.min.js"
                 "/js/ui.js"
                 "/js/processing.min.js"
                 )
               [:title "Combat map"]]
              [:body
               content
               (include-js
                 "//lesscss.googlecode.com/files/less-1.0.18.min.js"
                 "//netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/js/bootstrap.min.js"
                 )
               ]))

(defpartial layout-main [& [node-id content]]
            (html5
              [:head
               (include-css
                 "//netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/css/bootstrap.min.css"
                 "/css/global.css"
                 )
               (init-data {:initial-node (or (System/getenv "INITIAL_NODE")
                                             54)})
               [:link {:href "/less/styles.less",
                       :type "text/css",
                       :rel "stylesheet/less"}]
               (include-js
                 "http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"
                 "http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.16/jquery-ui.min.js"
                 "/js/ui.js"
                 "/js/processing.min.js"
                 )
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
                [:br {:style "clear: both;"}]]
               [:section#content
                content
                (include-js "/js/init.js")]
               [:aside
                [:div#explanation]
                [:div#browser_not_supported
                 "Your Browser is currently not supported."]]
               (include-js
                 "//lesscss.googlecode.com/files/less-1.0.18.min.js"
                 "//netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/js/bootstrap.min.js"
                 (str asset-host main-js-path))
               ]))

(defpartial node-details [{id :id {name :name} :data :as node}]
            [:div.node-details
             [:h2 name]])
