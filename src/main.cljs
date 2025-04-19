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
  (cond (#{"J" "Q" "K" "0"} rank) 10
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

(def player :dealer)

(def score 0)

(def deck nil)

;; (def interval nil)

;; (defn swap-cards! [n]
;;   (let [card (-> deck
;;                  first
;;                  :img)]
;;     (set! counter n)
;;     (change-card (str "card-" n) card))
;;   (if (= 1 (count deck))
;;     (js/clearInterval interval)
;;     (set! deck (drop 1 deck))))

;; (defn run-interval []
;;   (set! interval (js/setInterval (fn [e]
;;                                    (let [n (if (>= counter 4) 1 (inc counter))]
;;                                      (swap-cards! n)))
;;                                  120)))

(defn player-new-row [type hand]
  (case type
    :dealer
    (do
      (change-card (str "card-" 1) (first hand))
      (change-card (str "card-" 2) (second hand)))

    :player
    (do
      (change-card (str "card-" 3) (first hand))
      (change-card (str "card-" 4) (second hand)))))

(defn ace-new-score [hand score]
  (let [ace-count (count (filter #(= "A" (:rank %)) hand))]
    (loop [total     score
           ace-count ace-count]
      (if (and (> total 21) (> ace-count 0))
        (recur (- total 10) (dec ace-count))
        total))))

(defn new-turn []
  (let [ ;; switch player
        who (if (= player :player) :dealer :player)

        hand [(first deck) (second deck)]
        naipes (mapv #(:img %) hand)
        score (->> hand
                   (mapv #(:score %))
                   (reduce +))]
    (js/console.log naipes)
    (set! player who)
    (player-new-row who naipes)
    (set-score-value score)

    (when (some #{"A"} (mapv #(:rank %) hand))
      (set-score-value (ace-new-score hand score)))))

(defn game-run []
  (if (= 1 (count deck))
    (js/console.log "Game ended!!!")
    (do
      (new-turn)
      (set! deck (drop 2 deck))
      (js/console.log (count deck)))))

(defn init-eventhandlers []
  (.addEventListener (js/document.getElementById "hitme-btn") "click"
                     (fn []
                       (game-run)))
  (.addEventListener (js/document.getElementById "stay-btn") "click"
                     (fn []
                       (js/console.log "stay!!"))))

(defn start-game []
  (set! score 0)
  (set-score-value score)
  (set! deck (shuffle all-cards)))

(init-eventhandlers)

;; (js/window.addEventListener "load" #(run-interval))

(start-game)
