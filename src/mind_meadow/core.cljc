(ns mind-meadow.core
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))


(e/defn Window []
  (e/client
    (dom/div (dom/props {:class "window-frame"}) (dom/text "hello world"))))