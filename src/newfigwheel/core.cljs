(ns ^:figwheel-always newfigwheel.core
    (:require
      [reagent.core :as r]))

(defonce table-atom (r/atom {
  :selected-rows []
  :rows [0 1 2 3 4 5 6 7 8 9]}))

(defn element-data-id [element]
  (int (.getAttribute element "data-id")))

(defn timetable []
  (vec (concat [:table {:on-mouse-leave (fn [] (js/setTimeout #(swap! table-atom assoc :selected-rows []) 1500)) :on-mouse-move
      (js/customDrag (fn [e]
        (let [target (.-target e) tag-name (.-tagName target) data-id (element-data-id target)]
          (when (= tag-name "TD") 
            (swap! table-atom assoc :selected-rows (distinct (conj (@table-atom :selected-rows) data-id))) 
            ))))}]
    (vec (map (fn [row-i] [:tr (if (> (.indexOf (to-array (@table-atom :selected-rows )) row-i) -1) {:style {:background "green"}} {}) [:td {:data-id row-i} row-i ]]) (:rows @table-atom))))))

(.log js/console "lodeu")
(swap! table-atom assoc :selected-rows (conj (@table-atom :selected-rows) 4))
(r/render-component [timetable]  (.getElementById js/document "app"))
(swap! table-atom assoc :selected-rows [])
(deref table-atom)
