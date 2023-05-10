(ns mind-meadow.core
  (:import [hyperfiddle.electric Pending])
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric-ui4 :as ui]
            #?(:cljs [mind-meadow.listeners :refer [on-resize]])
            [mind-meadow.units :refer [px]]))


#?(:clj (defonce !nodes (atom {})))
(e/def nodes (e/server (e/watch !nodes)))
#?(:cljs (defonce !moving (atom #{})))
(e/def moving (e/watch !moving))


(defn align
  [ev node]
  (let [w (.-clientWidth (.-target ev))
        h (.-clientHeight (.-target ev))]
    ;(js/console.log w)
    ;(js/console.log h)
    ;(js/console.log (.-clientHeight (.-target ev)))
    (assoc node :x (int (- (:x node) (/ w 2)))
                :y (int (- (:y node) (/ h 2))))))


(e/defn add-node
  [ev]
  (let [node {:x (- (.-x ev) 10)
              :y (- (.-y ev) 15)}]
    (e/server (swap! !nodes assoc (str (random-uuid)) node))))


(e/defn Meadow
  []
  (e/server
    (e/for-by first [[id {:keys [x y height width]}] nodes]
      (e/client
        (dom/div
          (dom/on "mousedown" (e/fn [_] (swap! !moving conj id)))
          (dom/on "mousemove" (e/fn [ev]
                                (when (contains? moving id)
                                  (let [node (align ev {:x (.-x ev)
                                                        :y (.-y ev)})]
                                    (e/server (swap! !nodes (fn [m] (update-in m [id] merge node))))))))
          (dom/on "mouseup" (e/fn [_] (swap! !moving disj id)))
          (dom/on "mouseout" (e/fn [_] (swap! !moving disj id)))
          (dom/props {:class "node"
                      :contenteditable true
                      :id id
                      :style {:display (when id "block")
                              :height (px height)
                              :left (px x)
                              :top (px y)
                              :width (px width)}}))))))
          ;(when-let [target (new (on-resize dom/node))]
          ;  (when (not-empty (.-id (.-target target)))
          ;    (let [node {:height (.-height (.-contentRect target))
          ;                :width (.-width (.-contentRect target))}]
          ;      (e/server (swap! !nodes (fn [m] (update-in m [id] merge node))))))))))))


(e/defn Debug-bar
  []
  (e/client
    (dom/div (dom/props {:class "debug-bar"})
      (dom/div (dom/text (str nodes)))
      (dom/div (dom/text (str moving)))
      (ui/button (e/fn []
                   (reset! !moving #{})
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
