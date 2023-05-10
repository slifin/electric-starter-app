(ns mind-meadow.core
  (:import [hyperfiddle.electric Pending])
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric-ui4 :as ui]
            [missionary.core :as m]
            [mind-meadow.units :refer [px]]))


#?(:clj (defonce !nodes (atom {})))
(e/def nodes (e/server (e/watch !nodes)))

#?(:cljs
   (defn listen-for-resize [target-dom-elm]
     (m/observe (fn mount [emit!]
                  (emit! nil)
                  (let [resize-observer (js/ResizeObserver. (fn [entries _] (emit! entries)))]
                    (.observe resize-observer target-dom-elm #js{"box" "border-box"})
                    (fn unmount []
                      (.disconnect resize-observer)))))))


(e/defn add-node
  [ev]
  (let [node {:x (.-x ev)
              :y (.-y ev)}]
    (e/server (swap! !nodes assoc (str (random-uuid)) node))))


(e/defn delete-nodes
  []
  (e/server (reset! !nodes {})))


(e/defn Meadow
  []
  (e/server
    (e/for-by first [[id {:keys [x y height width]}] nodes]
      (e/client
        (dom/div
          (let [resized (new (listen-for-resize dom/node))]
            (e/for [target resized]
              (let [id (.-id (.-target target))
                    node {:height (.-height (.-contentRect target))
                          :width (.-width (.-contentRect target))}]
                (when (not= id "")
                  (e/discard
                    (e/server
                      (swap! !nodes (fn [m] (update-in m [id] merge node)))))))))
          (dom/props {:class "node"
                      :contenteditable true
                      :id id
                      :style {:top (px y) :left (px x)
                              :height (px height) :width (px width)}}))))))


(e/defn Window
  []
  (e/client
    (try
      (dom/div
        (dom/props {:class "window-frame"})
        (dom/on "dblclick" add-node)
        (Meadow.)
        (dom/div
          (dom/div (dom/text (str nodes)))
          (ui/button delete-nodes (dom/text "Delete"))))
      (catch Pending _
        (dom/style {:cursor "progress"})))))
