(ns main)

(def ^:dynamic *current-player* nil)

(def score 0)

(def ^:dynamic *deck* nil)

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
  (doall
   (for [[idx h] (map-indexed vector hand)
         :let [j (inc idx)]]
     (change-card (str "card-" (if (= type :dealer)
                                 j
                                 (+ 2 j))) h))))

(defn ace-new-score [hand score]
  (if (some (comp #{"A"} :rank) hand)
    (let [ace-count (count (filter #(= "A" (:rank %)) hand))]
      (loop [total     score
             ace-count ace-count]
        (if (and (> total 21) (> ace-count 0))
          (recur (- total 10) (dec ace-count))
          total)))
    score))

(defn new-turn [who]
  (let [hand [(first *deck*) (second *deck*)]
        naipes (mapv #(:img %) hand)
        score (->> hand
                   (mapv #(:score %))
                   (reduce +))]
    (set! *current-player* who)
    (player-new-row who naipes)

    (->> score
         (ace-new-score hand)
         set-score-value)))

(defn game-run []
  (new-turn (if (= *current-player* :player) :dealer :player))
  (set! *deck* (drop 2 *deck*))

  (when (zero? (count *deck*))
    (js/setTimeout
     (fn [_]
       (js/alert "No more cards! Game ended!!!"))
     500)))

(defn init-eventhandlers []
  (.addEventListener (js/document.getElementById "hitme-btn") "click"
                     (fn []
                       (when (= :player *current-player*)
                         (game-run)
                         (js/setTimeout
                           (fn [_]
                             (js/alert "end of game!\n You lost!")) 700))))
  (.addEventListener (js/document.getElementById "stay-btn") "click"
                     (fn []
                       (when-not (zero? (count *deck*))
                         (game-run)))))

(defn start-game []
  (set! *current-player* :dealer)
  (set! score 0)
  (set-score-value score)
  (set! *deck* (shuffle all-cards))
  (game-run))

(init-eventhandlers)

(start-game)
