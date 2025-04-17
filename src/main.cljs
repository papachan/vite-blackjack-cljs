(ns main)

(def ranks ["A"
            "K"
            "Q"
            "J"
            "0"
            "9"
            "8"
            "7"
            "6"
            "5"
            "4"
            "3"
            "2"])

(def suits #{"C" "D" "S" "H"})

(defn rank->score [rank]
  (cond (#{"J" "Q" "K"} rank) 10
        (#{"A"} rank) 11
        :else (parse-long rank)))

(def all-cards
  (into []
        (for [rank ranks
              suit suits]
          {:img (str rank suit) :rank rank :score (rank->score rank)})))

(defn change-card [id img]
  (set! (.-src (js/document.getElementById id)) (str "/assets/" img ".png")))

(defn set-score-value [v]
  (set! (.-innerText (js/document.getElementById "result")) v))

(def counter 0)

(def score 0)

(def deck (shuffle all-cards))

(def interval nil)

(defn swap-cards! [n]
  (let [card (-> deck
                 first
                 :img)]
    (set! counter n)
    (change-card (str "card-" n) card))
  (if (= 1 (count deck))
    (js/clearInterval interval)
    (set! deck (drop 1 deck))))

(defn run-interval []
  (set! interval (js/setInterval (fn [e]
                                   (let [n (if (>= counter 4) 1 (inc counter))]
                                     (swap-cards! n)))
                                 120)))

(defn player-new-row [type hand]
  (case type
    :dealer
    (do
      (change-card (str "card-" 1) (first hand))
      (change-card (str "card-" 2) (second hand)))

    :player
    (do
      (change-card (str "card-" 3) (first hand))
      (change-card (str "card-" 4) (second hand))))
  (set! deck (drop 2 deck))
  (js/console.log (count deck)))

(defn start-game []
  (set-score-value "0")
  (let [hand [(first deck) (second deck)]]
    (player-new-row :player (mapv #(:img %) hand)))
  (if (= 1 (count deck))
    (js/console.log "end games")))

(defn init-eventhandlers []
  (.addEventListener (js/document.getElementById "hitme-btn") "click"
                     (fn []
                       (js/console.log "hit me!!")))
  (.addEventListener (js/document.getElementById "stay-btn") "click"
                     (fn []
                       (js/console.log "stay!!"))))

(init-eventhandlers)

;; (js/window.addEventListener "load" #(run-interval))

(start-game)
