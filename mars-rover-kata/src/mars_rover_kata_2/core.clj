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

(defn meet-obstacle [rover]
  (update-in rover [:is-meet-obstacle] (fn [_] true)))

(def commands
  {:move-forward move-forward
   :move-backward move-backward
   :turn-left turn-left
   :turn-right turn-right
   :turn-opposite turn-opposite})

(def initial-rover
  {:position {:x 0 :y 0}
   :orientation :north
   :is-meet-obstacle false})

(def obstacles [{:x 2 :y 2} {:x 3 :y 3}])

(def planet {:width 10 :height 10})

(defn rover-can-move? [rover planet]
  (let [roverX (get-in rover [:position :x])
        roverY (get-in rover [:position :y])
        planetW (:width planet)
        planetH (:height planet)] (and (>= roverX 0) (>= roverY 0) (<= roverX planetW) (<= roverY planetH))))

(defn rover-hit-obstacle? [rover obstacles]
  (let [roverX (get-in rover [:position :x])
        roverY (get-in rover [:position :y])]
    (true? (some #(and (= roverX (get-in % [:x])) (= roverY (get-in % [:y]))) obstacles))))

(defn move [rover command planet obstacles]
  (let [cmd-fn (commands command)
        next-rover-move (cmd-fn rover)]
    (cond
      (rover-hit-obstacle? next-rover-move obstacles) (meet-obstacle rover)
      (rover-can-move? next-rover-move planet) next-rover-move
      :else rover)))


;; Non pure part of the app.

(def a-rover (atom initial-rover))

(defn move! [command planet obstacles]
  (swap! a-rover move command planet obstacles))

(def travel-route [:move-forward
                   :move-forward
                   :move-forward
                   :turn-right
                   :move-forward
                   :move-forward
                   :move-forward
                   :move-backward])

(defn rover-travel! [travel-route planet obstacles]
  (loop [i 0]
  (when (< i (count travel-route))
      (if (true? (get-in @a-rover [:is-meet-obstacle]))
        (print "Rover meet obstacle, stop!")
        (do 
             (move! (nth travel-route i) planet obstacles)
             (println "Rover go to" (get-in @a-rover [:position]))
             (recur (inc i))
        )))))

(rover-travel! travel-route planet obstacles)
