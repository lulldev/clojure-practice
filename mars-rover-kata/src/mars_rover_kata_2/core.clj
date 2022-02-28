(ns mars-rover-kata-2.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(def rover {:position {:x 0 :y 0} :orientation "North"})
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
