;; Bowling Rules 
;; The game consists of 10 frames. In each frame the player has two rolls to knock down 10 pins. 
;; The score for the frame is the total number of pins knocked down, plus bonuses 
;; for strikes and spares.

;; A spare is when the player knocks down all 10 pins in two rolls. 
;; The bonus for that frame is the number of pins knocked down by the next roll.

;; A strike is when the player knocks down all 10 pins on his first roll. 
;; The frame is then completed with a single roll. The bonus for that frame is 
;; the value of the next two rolls.

;; In the tenth frame a player who rolls a spare or strike is allowed to roll 
;; the extra balls to complete the frame. However no more than three balls can be rolled in tenth frame.

(def frames [[5 5]
             [2 1]])

(defn spare? [frame]
  (= (apply + frame) 10))

(defn strike? [frame]
  (= (first frame) 10))

(defn last-frame? [frames]
  (= (count frames) 3))

(defn update-last-frame [frames pins]
  (update frames (dec (count frames)) conj pins))

(defn roll [frames pins]
  (let [cur-frame (last frames)
        prev-frame (last (butlast frames))]
    (if (last-frame? frames)
      (cond
        (and (or (strike? prev-frame) (spare? prev-frame))
             (< (count cur-frame) 3))
        (update-last-frame frames pins)

        (< (count cur-frame) 2) (update-last-frame frames pins)
        :else frames)
      (cond
        (strike? prev-frame) (conj frames [pins])
        (= (count cur-frame) 2) (conj frames [pins])
        :else (update-last-frame frames pins)))))

(defn score [frames]
  (let [s frames]
    (loop [[prev & [cur nxt :as more]] (cons nil s)
           result []
           score 0]
      (if (seq more)
        (recur more (conj result [prev cur nxt score])
               (cond
                 (strike? prev) (+ score (* (apply + cur) 2))
                 (spare? prev) (+ score (apply + cur) (first cur))
                 :else (+ score (apply + cur))))
        score))))

(score frames)

(-> frames
    (roll 1)
    (roll 2)
    (roll 10))
