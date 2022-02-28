(ns mars-rover-kata-2.core
  (:gen-class))

;; Pure part

(def ->right {:north :est, :est :south, :south :west, :west :north})
(def ->left {:north :west, :west :south, :south :est, :est :north})
(def ->opposite {:north :south, :south :north, :est :west, :west :est})

(defn turn-left [rover] (update rover :orientation ->left))
(defn turn-right [rover] (update rover :orientation ->right))
(defn turn-opposite [rover] (update rover :orientation ->opposite))

(defn move-forward [rover]
  (condp = (:orientation rover)
    :north (update-in rover [:position :y] inc)
    :south (update-in rover [:position :y] dec)
    :east (update-in rover [:position :x] inc)
    :west (update-in rover [:position :x] dec)
    rover))

(def commands
  {:move-forward move-forward
   :turn-left turn-left
   :turn-right turn-right
   :turn-opposite turn-opposite})

(defn move [rover command]
  (let [cmd-fn (commands command)]
    (cmd-fn rover)))

(def initial-rover
  {:position {:x 0 :y 0}
   :orientation :north})

;; Let's quick check what is going on.
(comment
  (turn-left initial-rover) ;; _ <- place cursor into this place and evaluate the form
  (turn-right initial-rover)
  (turn-opposite initial-rover)
  (-> initial-rover
      move-forward
      move-forward
      turn-left
      move-forward)
  (move initial-rover :turn-left)
  )

;; Non pure part of the app.
;; In the best scenario this part is about 5-10% of the all application code.

(def a-rover (atom initial-rover))

(defn move! [command]
  (swap! a-rover move command))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(def rover )
(def planet {:width 5 :height 5})

(def rover-atom (atom rover))

(defn rover-turn-right []
  (swap! rover-atom assoc :orientation
         (cond
           (= (get @rover-atom :orientation) "North") "Est"
           (= (get @rover-atom :orientation) "Est") "South"
           (= (get @rover-atom :orientation) "South") "West"
           (= (get @rover-atom :orientation) "West") "North"
           :else (get @rover-atom :orientation))))

(defn rover-turn-left []
  (swap! rover-atom assoc :orientation
         (cond
           (= (get @rover-atom :orientation) "North") "West"
           (= (get @rover-atom :orientation) "West") "South"
           (= (get @rover-atom :orientation) "South") "Est"
           (= (get @rover-atom :orientation) "Est") "North"
           :else (get @rover-atom :orientation))))

(defn rover-turn-opposite []
  (swap! rover-atom assoc :orientation
         (cond
           (= (get @rover-atom :orientation) "North") "South"
           (= (get @rover-atom :orientation) "South") "North"
           (= (get @rover-atom :orientation) "Est") "West"
           (= (get @rover-atom :orientation) "West") "Est"
           :else (get @rover-atom :orientation))))

(defn rover-move-forward []
  (cond
    (= (get @rover-atom :orientation) "North") (swap! rover-atom update-in [:position :y] inc)
    (= (get @rover-atom :orientation) "West") (swap! rover-atom update-in [:position :x] dec)
    (= (get @rover-atom :orientation) "South") (swap! rover-atom update-in [:position :y] dec)
    (= (get @rover-atom :orientation) "Est") (swap! rover-atom update-in [:position :x] inc)
    :else (get @rover-atom :position)))

(defn rover-move-backward []
  (rover-turn-opposite)
  (rover-move-forward)
)

(rover-move-forward)
(rover-move-backward)
