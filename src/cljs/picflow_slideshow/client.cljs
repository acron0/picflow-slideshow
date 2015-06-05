(ns hello-clojurescript
  (:require [clojure.string :as string]
    [goog.dom :as dom]))

(def delay 10000) ;; 10s

(enable-console-print!)

(defn json-parse
  "Returns ClojureScript data for the given JSON string."
  [line]
  (js->clj (JSON/parse line)))

(defn focus-image [obj]
  (do
    (set! (. js/document -title) (get obj "desc"))
    (aset (.getElementById js/document "slideshowDesc-1") "innerHTML" (get obj "desc"))
    (dom/setProperties (.getElementById js/document "slideshowImage-1") (clj->js {:src (get obj "src")}))))

(defn doget [request-type url handler-function ]
  (let [x  (js/XMLHttpRequest.)  ]
    (aset  x "onreadystatechange" handler-function )
    (.open x request-type url)
    (.setRequestHeader x "Content-Type" "application/json" )
    (.setRequestHeader x "Accept" "application/json" )
    (.send x)))

(defn ajax-response-handler [content]
  (let [  targ (aget content "currentTarget")
          readyState (aget targ "readyState")
          resp (aget targ "response")
          readyState-ok (= readyState 4)]
          (if readyState-ok
            (if (= (aget targ "status") 200 )
              (focus-image (json-parse resp))))))

(defn get-new-picture []
  (doget "GET" "http://picflow.co/random" ajax-response-handler))

(defn doc-ready-handler []
  (let[ ready-state (. js/document -readyState)]
    (if (= "complete" ready-state)
      (do
        (get-new-picture)
        (js/setInterval get-new-picture delay)))))

(defn on-doc-ready []
  (aset  js/document "onreadystatechange" doc-ready-handler))

(on-doc-ready)

;;(defn handle-click []
;;  (js/alert "Hello!"))

;;(def clickable (.getElementById js/document "clickable"))
;;(.addEventListener clickable "click" handle-click)
