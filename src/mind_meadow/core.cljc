(ns mind-meadow.core
  (:import [hyperfiddle.electric Pending])
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [missionary.core :as m]
            [mind-meadow.units :refer [px]]))


#?(:clj (defonce !nodes (atom {})))
(e/def nodes (e/server (e/watch !nodes)))

#?(:cljs
   (defn listen-for-resize [target-dom-elm]
     (m/observe (fn mount [emit!]
                  (emit! nil)
                  (let [resize-observer (js/ResizeObserver.
                                          (fn [[entry] _]
                                            (emit! entry)))]
                    (.observe resize-observer target-dom-elm)
                    (fn unmount []
                      (.disconnect resize-observer)))))))


(e/defn add-node
  [e]
  (let [node {:x (.-x e)
              :y (.-y e)
              :width (.-width (.-target e))
              :height (.-height (.-target e))}
        id (random-uuid)]
    (e/server (swap! !nodes assoc id node))))


(e/defn Meadow
  []
  (e/server
    (e/for-by first [[id {:keys [x y width height]}] nodes]
      (e/client
        (dom/div
          (dom/props {:class "node"
                      :contenteditable true
                      :id id
                      :style {:top (str y "px") :left (px x)}})

          (dom/text (str nodes))
          (let [bk (new (listen-for-resize dom/node))]
            (js/console.log bk)))))))
            ;(e/server (swap! !nodes merge id {:width w :height h}))))))))


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
