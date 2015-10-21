(ns ^:figwheel-always newfigwheel.core
    (:require
      [reagent.core :as r]))

;; the atom that stores the "document".
(defonce table-atom
  (r/atom
   {:rows (into {}
                (for [n (range 100)]
                  [n {:selected? false
                      :content (str "cell " n)}]))}))

;; we store our user interface state here.
(defonce ui-atom
  (atom {}))

(defn select-cell-range
  "given a state atom value, returns a new value where
  all the rows from start to end have their :selected? key
  set to value"
  [state start end value]
  (reduce
   #(assoc-in %1 [:rows %2 :selected?] value)
   state
   (range (min start end) (inc (max start end)))))

(defn timetable []
  (let [state @table-atom]
    [:table {:style {:-webkit-user-select "none" :user-select "none"}}
     (map
      (fn [key]
        (let [{:keys [selected? content]} ((state :rows) key)]
          ^{:key key}
          [:tr [:td
                {:style {:background (if selected? "green" "none")}
                 :on-mouse-down #(let [destination (not selected?)]
                                   ;; destination holds what selected? should be set to. click-start remebers cell
                                   (swap! ui-atom assoc :click-start key :destination destination)
                                   ;; select this cell so click immediately selects
                                   (swap! table-atom assoc-in [:rows key :selected?] destination))
                 :on-mouse-enter #(let [{:keys [click-start destination]} @ui-atom]
                                    (when click-start
                                      ;; select from the click-start through to the present cell
                                      (swap! table-atom select-cell-range click-start key destination)
                                      ;; set click-start now to this cell to avoid re selecting cells over and over
                                      (swap! ui-atom assoc :click-start key)))
                 :on-mouse-up #(swap! ui-atom dissoc :click-start :destination)}
                content]]))
      (-> state :rows keys sort))]))

(r/render-component [timetable]  (.getElementById js/document "app"))
