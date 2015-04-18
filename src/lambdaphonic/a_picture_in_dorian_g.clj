(ns lambdaphonic.a-picture-in-dorian-g
  (:use [overtone.live :refer :all]
        [leipzig.melody :refer :all]
        [lambdaphonic.live.synths]
        [lambdaphonic.live.constants :refer :all])
  (require [leipzig.scale :as scale]
           [leipzig.live :as ll]
           [leipzig.chord :as chord]
           [leipzig.temperament :as temperament]))

(def pi-short (take 30 pi))

;; The complete score should be 3:14 long
(def score-length (+ (* 3 60) 14))
(def bar-length (/ score-length 60.0))

(def bass
  (->>
   (phrase (cycle [(* 4 bar-length)]) (map #(-> % scale/lower scale/lower) pi-short))
   (where :part (is :bass))))

(def _4th (/ bar-length 4))
(def _8th (/ bar-length 8))

(def melody
  (->>
    (phrase (cycle[_8th]) pi)
    (where :part (is :melody))))

(def kick
  (->>
   (phrase (cycle [_4th _4th _4th _4th _4th _4th _4th _8th _8th]) (repeat (* 4 4 30) 0))
   (where :part (is :kick))))

(def hat
  (->>
   (phrase (cons _8th (cycle [_4th]))
           (cons nil (repeat (* 4 4 30) 0)))
   (where :part (is :hat))))

(def blips
  (->>
    (phrase (cycle [(/ _8th 1)]) pi)
    (where :part (is :blips))))

(def track
  (->> blips
       (where :time (bpm 120))
       (where :duration (bpm 120))
       (where :pitch (comp scale/G scale/dorian))))

(defonce fx-bus (audio-bus))
(defonce main-g (group "main"))
(defonce fx-g (group "effects"))

(defmethod ll/play-note :bass [{midi :pitch}] (short-tone (midi->hz (- midi 12)) :dur (* 2 bar-length) :nharm 8 :amp 2))
;;(defmethod ll/play-note :kick [p] (bass-drum :level 0.8))
;;(defmethod ll/play-note :hat [p] (hat-drum :level 2))
(defmethod ll/play-note :melody [{midi :pitch dur :duration}] (short-tone (midi->hz midi) :dur dur :nharm 1 :amp 1))
(defmethod ll/play-note :blips [{midi :pitch dur :duration}] (sin-blip [:tail main-g] (+ 6000 (/ (* (- 140000 6000) (- midi 69)) 9)) :out-bus fx-bus))

(comment
  (ll/play track)
  (ll/stop)
  (recording-start "./The picture of Dorian Pi.wav")
  (recording-stop)
)
