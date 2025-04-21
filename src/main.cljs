(ns main)

(def player :dealer)

(def score 0)

(def deck nil)

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

(defn new-turn [who]
  (let [hand [(first deck) (second deck)]
        naipes (mapv #(:img %) hand)
        score (->> hand
                   (mapv #(:score %))
                   (reduce +))]
    (js/console.log naipes)
    (set! player who)
    (player-new-row who naipes)
    (set-score-value score)

    (when (some (comp #{"A"} :rank) hand)
      (js/console.log "Have an ace!!")
      (set-score-value (ace-new-score hand score)))))

(defn game-run []
  (new-turn (if (= player :player) :dealer :player))
  (set! deck (drop 2 deck))
  (js/console.log (count deck)

  (when (zero? (count deck))
    (js/setTimeout
      (fn [_]
        (js/alert "Game ended!!!"))
      700))))

(defn init-eventhandlers []
  (.addEventListener (js/document.getElementById "hitme-btn") "click"
                     (fn []
                       (when (= :player player)
                         (game-run)
                         (js/setTimeout
                           (fn [_]
                             (js/alert "end of game!\n You lost!")) 700))))
  (.addEventListener (js/document.getElementById "stay-btn") "click"
                     (fn []
                       (game-run))))

(defn start-game []
  (set! score 0)
  (set-score-value score)
  (set! deck (shuffle all-cards)))

(init-eventhandlers)

(start-game)
