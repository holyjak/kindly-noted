(ns kinds
  (:require [scicloj.kindly.v4.api :as kindly]
            [scicloj.kindly.v4.kind :as kind]
            [tablecloth.api :as tc]))

;; # Catalogue of visualization kinds

;; **WIP**

;; ## Markdown - `:kind/md`
(-> "hello *hello* **hello**"
    kind/md)

;; ## Code

(-> "(defn f [x] (+ x 9))"
    kind/code)

;; ## Vega-Lite

(def my-plot
  (-> {:encoding
       {:y {:field "y", :type "quantitative"},
        :size {:value 400},
        :x {:field "x", :type "quantitative"}},
       :mark {:type "circle", :tooltip true},
       :width 400,
       :background "floralwhite",
       :height 100,
       :data {:values "x,y\n1,1\n2,-4\n3,9\n", :format {:type "csv"}}}
      kind/vega-lite))

my-plot

;; ## Cytoscape

(def cytoscape-example
  {:elements {:nodes [{:data {:id "a" :parent "b"} :position {:x 215 :y 85}}
                      {:data {:id "b"}}
                      {:data {:id "c" :parent "b"} :position {:x 300 :y 85}}
                      {:data {:id "d"} :position {:x 215 :y 175}}
                      {:data {:id "e"}}
                      {:data {:id "f" :parent "e"} :position {:x 300 :y 175}}]
              :edges [{:data {:id "ad" :source "a" :target "d"}}
                      {:data {:id "eb" :source "e" :target "b"}}]}
   :style [{:selector "node"
            :css {:content "data(id)"
                  :text-valign "center"
                  :text-halign "center"}}
           {:selector "parent"
            :css {:text-valign "top"
                  :text-halign "center"}}
           {:selector "edge"
            :css {:curve-style "bezier"
                  :target-arrow-shape "triangle"}}]
   :layout {:name "preset"
            :padding 5}})

(kind/cytoscape
 cytoscape-example)

;; ## ECharts

;; This example is taken from Apache ECharts' [Getting Started](https://echarts.apache.org/handbook/en/get-started/).

(def echarts-example
  {:title {:text "Echarts Example"}
   :tooltip {}
   :legend {:data ["sales"]}
   :xAxis {:data ["Shirts", "Cardigans", "Chiffons",
                  "Pants", "Heels", "Socks"]}
   :yAxis {}
   :series [{:name "sales"
             :type "bar"
             :data [5 20 36
                    10 10 20]}]})

(kind/echarts
 echarts-example)


;; ## Plotly

(def plotly-example
  {:data [{:x [0 1 3 2]
           :y [0 6 4 5]
           :z [0 8 9 7]
           :type :scatter3d
           :mode :lines+markers
           :opacity 0.5
           :line {:width 5}
           :marker {:size 4
                    :colorscale :Viridis}}]})

(kind/plotly
 plotly-example)

;;; ## Images
(def clj-image
  (->  "https://upload.wikimedia.org/wikipedia/commons/e/eb/Ash_Tree_-_geograph.org.uk_-_590710.jpg"
       (java.net.URL.)
       (javax.imageio.ImageIO/read)))

clj-image

;;; ## Datasets
(def my-dataset
  (-> {:x (range 3)}
      tc/dataset
      (tc/map-columns :y
                      [:x]
                      (fn [x] (* x x)))))

my-dataset



;;; ## Reagent

(kind/reagent
 ['(fn [data]
     [:div {:style {:background "#ccddcc"}}
      [:big (count data)]])
  (vec (range 99))])


;; ## Pretty printing
(->> (range 30)
     (apply array-map)
     kind/pprint)


;; ## Hidden
(->> {:x 9}
     kind/hidden)

;; ## Plain data structures

;; Plain Clojure data structures have recursive kind semantics:
;; * Each tool has its own way to represent them visually
;; (e.g., Clay just uses text, while Portal has a hierarchical navigation UI).

(list 1 "A" :B 'C)

[1 "A" :B 'C]

#{1 "A" :B 'C}

{1 "A" :B 'C}


;; ## Plain data structures - nesting other kinds

;; * If the values inside them have kind information, they are handled accordingly.

[(kind/hiccup
  [:div {:style
         {:background-color "floralwhite"}}
   [:p "hello"]])
 (kind/md
  "hello *hello* **hello**")
 (kind/code
  "(defn f [x] (+ x 9))")]

{:x  (kind/md
      "**hello**")
 (kind/md
  "**hello**") :x}

;; ## Hiccup
(-> [:div {:style
           {:background-color "floralwhite"}}
     [:p "hello"]]
    kind/hiccup)

;; ## Hiccup - nesting other kinds
(-> [:div {:style
           {:background-color "floralwhite"
            :border-style "solid"}}
     [:p {:style {:background-color "#ccddcc"
                  :border-style "solid"}}
      "hello"]
     (kind/md
      "hello *hello* **hello**")
     (kind/code
      "(defn f [x] (+ x 9))")
     my-plot]
    kind/hiccup)

;; ## Portal

(-> [(kind/hiccup [:p {:style {:background-color "#ccddcc"
                               :border-style "solid"}}
                   "hello"])
     (kind/md
      "hello *hello* **hello**")
     (kind/code
      "(defn f [x] (+ x 9))")
     my-plot]
    kind/portal)
