(ns mind-meadow.core
  (:import [hyperfiddle.electric Pending])
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [missionary.core :as m]
            [mind-meadow.units :refer [px]]))


#?(:clj (defonce !nodes (atom {})))
(e/def nodes (e/server (e/watch !nodes)))
;#?(:cljs (defonce motion? (atom false)))


;(e/defn listen-for-resize
;  [[entry] _]
;  (let [{:keys [width]} (.-contentRect entry)]
;    (e/server (swap! !nodes merge
;                     (.-id (.-target entry))
;                     {:width width}))))
;
;
;(e/defn on-resize
;  [target]
;  (.observe (new js/ResizeObserver listen-for-resize) target))
;

(e/defn add-node
  [e]
  (let [node {:x (.-x e)
              :y (.-y e)
              :width (.-width (.-target e))
              :height (.-height (.-target e))}]
    (e/server (swap! !nodes assoc (random-uuid) node))))


;(e/defn move-node
;  [e]
;  (when @motion?
;    (let [node {:x (.-offsetX e)
;                :y (.-offsetY e)}
;          id (uuid (.-id (.-target e)))]
;      (e/server (swap! !nodes merge {id node})))))
;

(e/defn Meadow
  []
  (e/server
    (e/for-by first [[id {:keys [x y width height]}] nodes]
      (e/client
        (dom/div
          (dom/props {:class "node"
                      :contenteditable true
                      :id id
                      :style {:top (px y) :left (px x)
                              :height (px height) :width (px width)}})
          ;(dom/on "mousemove" move-node)
          ;(dom/on "mousedown" (e/fn [_] (reset! motion? true)))
          ;(dom/on "mouseup" (e/fn [_] (reset! motion? false)))
          (dom/text ""))))))


(e/defn Window
  []
  (e/client
    (try
      (dom/div
        (dom/props {:class "window-frame"})
        (dom/on "dblclick" add-node)
        (Meadow.))
      (catch Pending _
        (dom/style {:cursor "progress"})))))
