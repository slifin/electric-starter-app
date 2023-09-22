(ns mind-meadow.debug
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric-ui4 :as ui]))


(e/defn Debug-bar
  [nodes !nodes moving !moving]
  (dom/div
    (dom/props {:class "debug-bar"})
    (dom/div (dom/text (str nodes)))
    (dom/div (dom/text (str moving)))
    (ui/button (e/fn []
                     (reset! !moving {})
                     (e/server (reset! !nodes {})))
               (dom/text "Delete"))))