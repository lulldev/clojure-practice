((ns mars-rover-kata-2.core
  (:gen-class))

(def planet-data (slurp "planet.txt"))
(def obstacles-data (slurp "obstacles.txt"))
(def rover-data (slurp "rover.txt"))

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

(defn rover-hit-obstacle? [rover obstacles]
  (boolean (some (partial = (:position rover)) obstacles)))

(defn move [rover command planet obstacles]
  (let [cmd-fn (command-handlers command)
        next-rover (cmd-fn rover)]
    (cond
      (rover-hit-obstacle? next-rover obstacles) {:status :obstacle :rover rover}
      (rover-hit-planet-edge? rover planet) {:status :planet-edge :rover rover}
      :else  {:status :ok :rover next-rover})))


(defn parse-planet-from-str [str] ()
  (let [[_ width height] (re-matches #"([0-9]+)x([0-9]+)" str)]
    (if (nil? _) nil {:width (Integer/parseInt width) :height (Integer/parseInt height)})))

(defn parse-obstacles-from-str [str]
  (let [obstacles (re-seq #"([0-9]+),([0-9]+)" str)]
    (if (> (count obstacles) 0)
      (map #(let [[_ x y] %] {:x (Integer/parseInt x) :y (Integer/parseInt y)}) obstacles)
      nil
      )))

(defn parse-rover-from-str [str]
  (let [[_ x y orientation] (re-matches #"([0-9]+),([0-9]+):(W|S|E|N)" str)]
    (if (nil? _) nil {:position {:x (Integer/parseInt x) :y (Integer/parseInt y)}
                      :orientation (condp = orientation
                                        "N" :north
                                        "W" :west
                                        "S" :south
                                        "E" :east
                                        nil)})))

(defn travel [rover commands obstacles planet]
  (loop [r rover
         [cmd & other-cmds] commands
         result []]
    (if cmd
      (let [move-result (move r cmd planet obstacles)]
        (recur (:rover move-result) other-cmds (conj result move-result)))
      result)))

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
  (let [
        initial-rover (parse-rover-from-str rover-data)
        obstacles (parse-obstacles-from-str obstacles-data)
        planet (parse-planet-from-str planet-data)
        statuses (travel initial-rover cmds obstacles planet)]
    (doseq [s statuses]
      (let [msg (condp = (:status s)
                  :obstacle "Rover met obstacle, stop!"
                  :planet-edge "Rover is on planet edge, stop!"
                  :ok (str "Rover go to " (-> s :rover :position)))]
        (println msg)))))

(comment
  (main)
  )
)