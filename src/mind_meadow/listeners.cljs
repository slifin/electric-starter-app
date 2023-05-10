(ns mind-meadow.listeners
  (:require [missionary.core :as m]))


(defn listen-for-resize [target-dom-elm]
  (->>
    (m/observe (fn mount [emit!]
                 (emit! nil)
                 (let [resize-observer (js/ResizeObserver. (fn [entries _] (emit! entries)))]
                   (.observe resize-observer target-dom-elm #js{"box" "border-box"})
                   (fn unmount []
                     (.disconnect resize-observer)))))
    (m/relieve {})))