(ns mind-meadow.core
  (:import [hyperfiddle.electric Pending])
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric-ui4 :as ui]
            [mind-meadow.units :refer [px]]))


#?(:clj (defonce !nodes (atom {})))
(e/def nodes (e/server (e/watch !nodes)))
#?(:cljs (defonce !moving (atom {})))
(e/def moving (e/watch !moving))



(e/defn add-node
  [ev]
  (let [node {:x (- (.-x ev) 45)
              :y (- (.-y ev) 17)}]
    (e/server (swap! !nodes assoc (str (random-uuid)) node))))


(e/defn Meadow
  []
  (e/server
    (e/for-by first [[id {:keys [x y height width]}] nodes]
      (e/client
        (dom/div
          (dom/on "mousedown" (e/fn [ev]
                                (swap! !moving assoc id {:w (.-layerX ev)
                                                         :h (.-layerY ev)})))
          (dom/on "mousemove" (e/fn [ev]
                                (when (contains? moving id)
                                  (let [{:keys [w h]} (get moving id)
                                        node {:x (- (.-x ev) w)
                                              :y (- (.-y ev) h)}]
                                    (e/server (swap! !nodes (fn [m] (update-in m [id] merge node))))))))
          (dom/on "mouseup" (e/fn [_] (swap! !moving dissoc id)))
          (dom/on "mouseout" (e/fn [_] (swap! !moving dissoc id)))
          (dom/props {:class "node"
                      :contenteditable true
                      :id id
                      :style {:display (when id "block")
                              :height (px height)
                              :left (px x)
                              :top (px y)
                              :width (px width)}}))))))


(e/defn Debug-bar
  []
  (e/client
    (dom/div (dom/props {:class "debug-bar"})
      (dom/div (dom/text (str nodes)))
      (dom/div (dom/text (str moving)))
      (ui/button (e/fn []
                   (reset! !moving {})
                   (e/server (reset! !nodes {})))
                 (dom/text "Delete")))))


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