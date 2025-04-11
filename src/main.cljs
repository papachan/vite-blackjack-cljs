(ns main)

(defn element [tag id child-of prepend?]
  (or (js/document.getElementById id)
      (let [elt (js/document.createElement tag)
            parent (if child-of (js/document.querySelector child-of)
                       js/document.body)]
        (set! elt -id id)
        (if prepend?
          (.prepend parent elt)
          (.appendChild parent elt))
        elt)))

(defonce create-h1-title
  (element "h1" "title" "#app" true))

(let [el (js/document.getElementById "title")]
  (set! (.-innerText el) "Hello World from clojurescript")
  (.setAttribute el "class" "font-semibold text-3xl text-gray-500 mb-6"))
