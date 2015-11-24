(ns ^:figwheel-always newfigwheel.core
    (:require
      [reagent.core :as r]))

(def width 6)
(def height 40)

;; the atom that stores the "document".
(defonce table-atom
  (r/atom
   (into {}
         (for [x (range width) y (range height)]
           [[x y] {:selected? false
                   :content (str "(" x "," y ")")}]))))

;; we store our user interface state here.
(defonce ui-atom
  (atom {}))

(defn- select-cell-range
  "given a state atom value, returns a new value where
  all the rows from start to end have their :selected? key
  set to 'destination'"
  [state [sx sy] [ex ey] destination]
  (reduce
   #(assoc-in %1 [%2 :selected?] destination)
   state
   (for [x (range (min sx ex) (inc (max sx ex)))
         y (range (min sy ey) (inc (max sy ey)))]
     [x y])))

(defn timetable []
  (let [state @table-atom]
    ^{:key :table} [:table {:style {:-webkit-user-select "none" :user-select "none"}
                 :on-mouse-leave #(swap! ui-atom dissoc :click-start)}
         (for [y (range height)]
           ^{:key [:row y]}
           [:tr
            (for [x (range width)]
              (let [pos [x y]
                    {:keys [selected? content]} (state [x y])]
                ^{:key pos} [:td
                             {:style {:background (if selected? "green" "none")}
                              :on-mouse-down #(let [destination (not selected?)]
                                                ;; destination holds what selected? should be set to. click-start remebers cell
                                                (swap! ui-atom assoc :click-start [x y] :destination destination)
                                                ;; select this cell so click immediately selects
                                                (swap! table-atom assoc-in [[x y] :selected?] destination))
                              :on-mouse-enter #(let [{:keys [click-start destination]} @ui-atom]
                                                 (when click-start
                                                   ;; select from the click-start through to the present cell
                                                   (swap! table-atom select-cell-range click-start [x y] destination)))
                              :on-mouse-up #(swap! ui-atom dissoc :click-start :destination)}
                             content]))])]))

(r/render-component [timetable]  (.getElementById js/document "app"))
