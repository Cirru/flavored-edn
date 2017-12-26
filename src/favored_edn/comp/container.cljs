
(ns favored-edn.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.macros :refer [defcomp cursor-> <> div button textarea span]]
            [verbosely.core :refer [verbosely!]]
            [respo.comp.space :refer [=<]]
            [reel.comp.reel :refer [comp-reel]]
            [cljs.reader :refer [read-string]]
            [favored-edn.core :refer [write-edn]]))

(defn program [text] (write-edn (read-string text)))

(def style-code
  (merge
   ui/textarea
   {:width 640,
    :height "100%",
    :font-size 12,
    :font-family "Source Code Pro, Menlo, monospace",
    :flex 1}))

(defcomp
 comp-container
 (reel)
 (let [store (:store reel), states (:states store)]
   (div
    {:style (merge ui/global ui/row)}
    (textarea
     {:value (:content store),
      :placeholder "Content",
      :style style-code,
      :spell-check false,
      :on-input (fn [e d! m!] (d! :content (:value e)))})
    (=< 1 nil)
    (textarea
     {:value (:result store),
      :placeholder "Content",
      :style style-code,
      :spell-check false,
      :on-input (fn [e d! m!] )})
    (=< "8px" nil)
    (div
     {:style {:position :absolute, :top 8, :right 8}}
     (button
      {:style ui/button,
       :inner-text (str "run"),
       :on {:click (fn [e d! m!] (d! :result (program (:content store))))}}))
    (cursor-> :reel comp-reel states reel {}))))
