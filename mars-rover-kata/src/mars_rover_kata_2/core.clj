(ns mars-rover-kata-2.core
  (:gen-class))

(def ->right {:north :east, :east :south, :south :west, :west :north})
(def ->left {:north :west, :west :south, :south :east, :east :north})
(def ->opposite {:north :south, :south :north, :east :west, :west :east})

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

(defn move-backward [rover]
  (-> rover turn-opposite move-forward))

(def command-handlers
  {:move-forward  move-forward
   :move-backward move-backward
   :turn-left     turn-left
   :turn-right    turn-right
   :turn-opposite turn-opposite})

(defn rover-hit-planet-edge? [rover planet]
  (let [x (get-in rover [:position :x])
        y (get-in rover [:position :y])
        planet-w (:width planet)
        planet-h (:height planet)]
    (or (neg? x) (neg? y) (> x planet-w) (> y planet-h))))
(rover-hit-planet-edge? initial-rover planet)

(defn rover-hit-obstacle? [rover obstacles]
  (boolean (some (partial = (:position rover)) obstacles)))

(defn move [rover command planet obstacles]
  (let [cmd-fn (command-handlers command)
        next-rover (cmd-fn rover)]
    (cond
      (rover-hit-obstacle? next-rover obstacles) {:status :obstacle :rover rover}
      (rover-hit-planet-edge? rover planet) {:status :planet-edge :rover rover}
      :else  {:status :ok :rover next-rover})))


;; (defn rover-travel! [travel-route planet obstacles]
;;   (loop [i 0]
;;   (when (< i (count travel-route))
;;       (if (true? (get-in @a-rover [:is-meet-obstacle]))
;;         (print "Rover meet obstacle, stop!")
;;         (do
;;              (move! (nth travel-route i) planet obstacles)
;;              (println "Rover go to" (get-in @a-rover [:position]))
;;              (recur (inc i))
;;              )))))

(defn travel [rover commands obstacles planet]
  (loop [r rover
         [cmd & other-cmds] commands
         result []]
    (if cmd
      (let [move-result (move r cmd planet obstacles)]
        (recur (:rover move-result) other-cmds (conj result move-result)))
      result)))

(def initial-rover
  {:position    {:x 0 :y 0}
   :orientation :north})

(def obstacles [{:x 2 :y 2} {:x 3 :y 3}])

(def planet {:width 10 :height 10})

(def cmds
  [:move-forward
   :move-forward
   :move-forward
   :turn-right
   :move-forward
   :move-forward
   :move-forward
   :move-backward])

(defn main []
  (let [statuses (travel initial-rover cmds obstacles planet)]
    (doseq [s statuses]
      (let [msg (condp = (:status s)
                  :obstacle "Rover met obstacle, stop!"
                  :planet-edge "Rover is on planet edge, stop!"
                  :ok (str "Rover go to " (-> s :rover :position)))]
        (println msg)))))

(comment
  (main)
  )
