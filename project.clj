(defproject combat "0.1.0-SNAPSHOT"
            :plugins [[lein-cljsbuild "0.2.9"]]
            :description "FIXME: write this!"
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [jayq "0.2.3"]
                           [fetch "0.1.0-alpha2"]
                           [crate "0.2.1"]
                           [noir "1.3.0-beta3"]
                           [clojurewerkz/neocons "1.0.2"]]
            :min-lein-version "2.0.0"
            :repl-options {:nrepl-middleware
                           [cemerick.piggieback/wrap-cljs-repl]}
            :cljsbuild {:builds
                        {:dev
                         {:source-path "src/cljs"
                          :compiler
                          {:output-to "resources/public/js/bin-debug/main.js"
                           :output-dir "resources/public/js/bin-debug"
                           :optimizations :whitespace
                           :pretty-print true}}
                         }}
            :main combat.server)
