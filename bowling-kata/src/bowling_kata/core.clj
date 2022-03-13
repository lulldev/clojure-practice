(ns bowling-kata.core)
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


(defn spare? [frame]
  (and (= (count frame) 2)
       (= (apply + frame) 10)))

(defn strike? [frame]
  (= (first frame) 10))

(defn last-frame? [frames]
  (= (count frames) 10))

(defn append-to-last-frame [frames pins]
  (update frames (dec (count frames)) conj pins))

(defn roll [frames pins]
  (let [cur-frame (last frames)
        prev-frame (last (butlast frames))]
    (if (last-frame? frames)
      (cond
        (and (or (strike? prev-frame) (spare? prev-frame))
             (< (count cur-frame) 3))
        (append-to-last-frame frames pins)

        (< (count cur-frame) 2) (append-to-last-frame frames pins)
        :else frames)
      (if (or (strike? cur-frame) (= (count cur-frame) 2))
        (conj frames [pins])
        (append-to-last-frame frames pins)))))

;; (defn score [frames]
;;   (let [s frames]
;;     (loop [[prev & [cur nxt :as more]] (cons nil s)
;;            result []
;;            score 0]
;;       (if (seq more)
;;         (recur more (conj result [prev cur nxt score])
;;                (cond
;;                  (strike? prev) (+ score (* (apply + cur) 2))
;;                  (spare? prev) (+ score (apply + cur) (first cur))
;;                  :else (+ score (apply + cur))))
;;         score))))

(defn score
  "Считаем очки текущего хода, прибавляя очки из следующего хода, если нужно.
  Если в текущем ходе был spare, добавляем очки из первого броска следующего хода.
  Если в текущем ходе был strike, добавляем очки двух бросков следующего хода."
  [frames]
  (+ (reduce (fn [acc [a b]]
               (let [bonus (cond
                             (spare? a) (first b)
                             (strike? a) (apply + (take 2 b))
                             :else 0)]
                 (apply + acc bonus a)))
             0
             (map vector frames (rest frames)))
     (apply + (last frames))))

(comment
  (def initial [[]])
  (-> initial
      (roll 5) (roll 5)
      (roll 1) (roll 2)
      (roll 10)
      (roll 3) (roll 2)
      score)
  )
