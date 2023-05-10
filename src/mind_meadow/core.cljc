(ns mind-meadow.core
  (:import [hyperfiddle.electric Pending])
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric-ui4 :as ui]
            #?(:cljs [mind-meadow.listeners :refer [listen-for-resize]])
            [mind-meadow.units :refer [px]]))


#?(:clj (defonce !nodes (atom {})))
(e/def nodes (e/server (e/watch !nodes)))


(e/defn add-node
  [ev]
  (let [node {:x (.-x ev)
              :y (.-y ev)}]
    (e/server (swap! !nodes assoc (str (random-uuid)) node))))


(e/defn Meadow
  []
  (e/server
    (e/for-by first [[id {:keys [x y height width]}] nodes]
      (e/client
        (dom/div
          (dom/props {:class "node"
                      :contenteditable true
                      :id id
                      :style {:top (px y)
                              :left (px x)
                              :height (px height)
                              :width (px width)}})
          (let [resized (new (listen-for-resize dom/node))]
            (e/for [target resized]
              (let [node {:height (.-height (.-contentRect target))
                          :width (.-width (.-contentRect target))}]
                (if (not= "" (.-id (.-target target)))
                 (e/server
                   (swap! !nodes (fn [m] (update-in m [id] merge node))))
                 (do
                   (js/console.log (.-target target))
                   (js/console.log target)))))))))))

(e/defn Debug-bar
  []
  (e/client
    (dom/div (dom/props {:class "debug-bar"})
      (dom/div (dom/text (str nodes)))
      (ui/button (e/fn [] (e/server (reset! !nodes {}))) (dom/text "Delete")))))


(e/defn Window
  []
  (e/client
    (try
      (Debug-bar.)
      (dom/div
        (dom/props {:class "window-frame"})
        (dom/on "dblclick" add-node)
        (Meadow.))
      (catch Pending _
        (dom/style {:cursor "progress"})))))
