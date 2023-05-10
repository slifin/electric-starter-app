(ns mind-meadow.listeners
  (:require [missionary.core :as m]))


(defn on-resize [target-dom-elm]
  (->>
    (m/observe (fn mount [emit!]
                 (emit! nil)
                 (let [resize-observer (js/ResizeObserver.
                                         (fn [entries _]
                                           (for [entry entries]
                                             (emit! entry))))]
                   (.observe resize-observer target-dom-elm)
                   (fn unmount []
                     (.disconnect resize-observer)))))
    (m/relieve {})))