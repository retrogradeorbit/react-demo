(ns ^:figwheel-always newfigwheel.core
    (:require
      [reagent.core :as r]))

(defonce table-atom (r/atom {
  :selected-rows []
  :rows (range 0 100)}))

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

(r/render-component [timetable]  (.getElementById js/document "app"))
