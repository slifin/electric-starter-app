(ns mind-meadow.listeners
  (:require [missionary.core :as m]))


(defn listen-for-resize [target-dom-elm]
  (m/observe (fn mount [emit!]
               (emit! target-dom-elm)
               (let [resize-observer (js/ResizeObserver.
                                       (fn [[entry] _]
                                         (emit! (.-target entry))))]
                 (.observe resize-observer target-dom-elm)
                 (fn unmount []
                   (.disconnect resize-observer))))))