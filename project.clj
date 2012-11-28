(defproject combat "0.1.0-SNAPSHOT"
            :plugins [[lein-cljsbuild "0.2.10"]]
            :description "combat maps"
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [org.clojure/clojurescript "0.0-1535"]
                           [org.clojure/google-closure-library "0.0-2029"]
                           [org.clojure/google-closure-library-third-party "0.0-2029"]
                           [jayq "0.2.3"]
                           [fetch "0.1.0-alpha2"]
                           [crate "0.2.1"]
                           [noir "1.3.0-beta3"]
                           [com.cemerick/friend "0.1.2"]
                           [clojurewerkz/neocons "1.0.2"]]
            :min-lein-version "2.0.0"
            :cljsbuild {:builds
                        {:dev
                         {:source-path "src/cljs"
                          :compiler
                          {:output-to "resources/public/js/bin-debug/main.js"
                           :output-dir "resources/public/js/bin-debug"
                           :optimizations :whitespace
                           :pretty-print true}}
                         :prod
                         {:source-path "src/cljs"
                          :compiler
                          {:output-to "resources/public/js/bin/main.js"
                           :output-dir "resources/public/js/bin"
                           :optimizations :whitespace
                           :pretty-print true}}
                         }}
            :s3 {:bucket "combat-app"
                 :root "resources/public/"
                 :files ["js/bin/main.js"]}
            :main combat.server)
