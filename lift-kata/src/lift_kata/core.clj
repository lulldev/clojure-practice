;; The Lift Kata
;; https://github.com/softwarecrafters/kata-log/blob/master/_katas/lift-kata.md
;;
;; a lift responds to calls containing a source floor and direction
;; a lift has an attribute floor, which describes it's current location
;; a lift delivers passengers to requested floors
;; you may implement current floor monitor
;; you may implement direction arrows
;; you may implement doors (opening and closing)
;; you may implement DING!
;; there can be more than one lift

(ns lift-kata.core
  (:gen-class))

(def initial-lift {:doors-open false
                   :motion :stop ;; :up :down :stop
                   :cur-floor 1
                   :floor-queue '()})

(defn open-door [lift]
  (assoc lift :doors-open true))

(defn close-door [lift]
  (assoc lift :doors-open false))

(defn call-lift [lift floor]
  (-> lift (assoc :floor-queue (cons floor (:floor-queue lift)))))

(defn choose-floor [lift floor]
  (-> lift (call-lift floor) close-door))

(defn move-lift [lift] (let [fq (get lift :floor-queue)]
                         (if-not (empty? fq) 
                           (-> lift
                               close-door
                               (assoc :cur-floor (first fq) :floor-queue (rest fq))
                               open-door)
                           lift)))

(defn command-panel [lift] (do (print {:cur-floor 1
                                       :buttons [{:floor 1 :active true}
                                                 {:floor 1 :active false}
                                                 {:floor 1 :active true}]})) lift)

(-> initial-lift
    (call-lift 3)
    (move-lift)
    (choose-floor 2)
    )

