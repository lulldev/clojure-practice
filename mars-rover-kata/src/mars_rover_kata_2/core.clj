(ns mars-rover-kata-2.core
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn load-data [filename]
  (with-open [r (io/reader filename)]
    (edn/read (java.io.PushbackReader. r))))

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
    (or (neg? x) (neg? y)
        (> x planet-w) (> y planet-h))))

(defn rover-hit-obstacle? [rover obstacles]
  (boolean (some (partial = (:position rover)) obstacles)))

(defn move [rover command planet obstacles]
  (let [cmd-fn (command-handlers command)
        next-rover (cmd-fn rover)]
    (cond
      (rover-hit-obstacle? next-rover obstacles) {:status :obstacle :rover rover}
      (rover-hit-planet-edge? rover planet)      {:status :planet-edge :rover rover}
      :else                                      {:status :ok :rover next-rover})))

(defn travel [rover commands obstacles planet]
  (loop [r rover
         [cmd & other-cmds] commands
         result []]
    (if cmd
      (let [move-result (move r cmd planet obstacles)]
        (recur (:rover move-result) other-cmds (conj result move-result)))
      result)))

(defn main []
  (let [rover     (load-data "resources/rover.edn")
        obstacles (load-data "resources/obstacles.edn")
        planet    (load-data "resources/planet.edn")
        commands  [:move-forward
                   :move-forward
                   :move-forward
                   :turn-right
                   :move-forward
                   :move-forward
                   :move-forward
                   :move-backward]
        progress   (travel rover commands obstacles planet)]
    (doseq [s progress]
      (let [msg (condp = (:status s)
                  :obstacle    "Rover met obstacle, stop!"
                  :planet-edge "Rover is on planet edge, stop!"
                  :ok          (str "Rover go to " (-> s :rover :position)))]
        (println msg)))))

(comment
  (main)
  )
