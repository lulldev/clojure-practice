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
  (assoc lift :floor-queue (cons floor (:floor-queue lift))))

(defn choose-floor [lift floor]
  (-> lift close-door (call-lift floor)))

(defn move-lift [lift] (let [fq (get lift :floor-queue)]
                         (if-not (empty? fq)
                           (assoc lift :floor-queue (rest fq))
                           lift)))

(-> initial-lift
    (call-lift 3)
    (call-lift 8)
    (call-lift 1)
    (move-lift)
    (call-lift 5)
    (move-lift)
)

