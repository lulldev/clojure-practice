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

(def commands
  {:move-forward move-forward
   :move-backward move-backward
   :turn-left turn-left
   :turn-right turn-right
   :turn-opposite turn-opposite})

(def initial-rover
  {:position {:x 0 :y 0}
   :orientation :north})

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
    (if (and (rover-can-move? next-rover-move planet) (not (rover-hit-obstacle? next-rover-move obstacles)))
      next-rover-move
      rover)))


;; Non pure part of the app.

(def a-rover (atom initial-rover))

(defn move! [command planet obstacles]
  (swap! a-rover move command planet obstacles))

(move! :turn-right planet obstacles)
(move! :move-forward planet obstacles)
