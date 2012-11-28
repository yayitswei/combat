(ns combat.views.welcome
  (:require [combat.views.common :as common]
            [noir.content.getting-started]
            [combat.graph :as graph])
  (:use [noir.core :only [defpage]]
        [noir.response :only [json]]))

(defpage
  "/" {:keys [id]}
  (common/layout id))

(defpage
  [:get "/resources/show"] {:keys [id]}
  (json
    (graph/get-resource (read-string id))))
