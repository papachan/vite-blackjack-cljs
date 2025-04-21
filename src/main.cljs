(ns main)

(def ^:dynamic *current-player* nil)

(def ^:dynamic *score* 0)

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

(defn enable-disable-btns [bool]
  (set! (.-disabled (js/document.getElementById "hitme-btn")) (not bool))
  (set! (.-disabled (js/document.getElementById "stay-btn")) (not bool)))

(defn change-card [id img]
  (set! (.-src (js/document.getElementById id)) (str "/assets/" img ".png")))

(defn set-score-value [v]
  (set! (.-innerText (js/document.getElementById "result")) v))

(defn render-new-cards [type hand]
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

(defn whois []
  (if (= :player *current-player*)
    "You"
    "Dealer"))

(defn check-score [score]
  (cond
    (= 21 score) (js/setTimeout
                  (fn [_]
                    (enable-disable-btns false)
                    (js/alert (str (whois) " wins !!!")))
                  500)
    (= score *score*)
    (js/console.log "It's a tie!")
    (> score 21)
    (js/console.log (str (whois) " wins!!!"))
    (< score *score*)
    (js/console.log (str (whois) " wins!!!"))
    :else
    (js/console.log (str (whois) " wins!!!"))))

(defn new-turn [who]
  (let [hand [(first *deck*) (second *deck*)]
        naipes (mapv #(:img %) hand)]
    (set! *current-player* who)
    (render-new-cards who naipes)

    ;; compare scores
    (let [score (->> hand
                     (mapv #(:score %))
                     (reduce +)
                     (ace-new-score hand))]
      (js/console.log "score:" score " - old score" *score*)
      (when-not (zero? *score*)
        (check-score score))
      (set-score-value score)
      (set! *score* score))))

(defn game-run []
  (enable-disable-btns false)
  (new-turn (if (= *current-player* :player) :dealer :player))
  (set! *deck* (drop 2 *deck*))

  (when (zero? (count *deck*))
    (js/setTimeout
     (fn [_]
       (enable-disable-btns false)
       (js/alert "No more cards! Game ended!!!"))
     500))

  (enable-disable-btns true))

(defn init-eventhandlers []
  (.addEventListener (js/document.getElementById "hitme-btn") "click"
                     (fn []
                       (when (and (= :player *current-player*)
                                  (not (zero? (count *deck*))))
                         (game-run)
                         ;; (js/setTimeout
                         ;;   (fn [_]
                         ;;     (js/alert "end of game!\n You lost!")) 500)
                         )))
  (.addEventListener (js/document.getElementById "stay-btn") "click"
                     (fn []
                       (when-not (zero? (count *deck*))
                         (game-run)))))

(defn start-game []
  (set! *current-player* :dealer)
  (set! *score* 0)
  (set-score-value *score*)
  (set! *deck* (shuffle all-cards))
  (game-run))

(init-eventhandlers)

(start-game)
