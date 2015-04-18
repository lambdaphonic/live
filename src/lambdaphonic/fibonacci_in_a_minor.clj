(ns lambdaphonic.fibonacci-in-a-minor
  (:use [overtone.live :refer :all]
        [leipzig.melody :refer :all]
        [lambdaphonic.live.synths])
  (require [leipzig.scale :as scale]
           [leipzig.live :as ll]
           [leipzig.chord :as chord]
           [leipzig.temperament :as temperament]))

(def chord-mappings {:C4 [:E4 :G3] :C#4 [:A3 :G3] :D4 [:C4 :G3] :D#4 [:C4 :A3] :Eb4 [:C4 :A3] :E4 [:C4 :A4] :F4 [:D4 :A4] :F#4 [:D4 :A4] :G4 [:E4 :B4] :G#4 [:E4 :B4] :Ab4 [:E4 :B4] :A4 [:E4 :C4] :A#4 [:F4 :D4] :Bb4 [:F4 :D4] :B4 [:G4 :D4]})

(defn get-chord [midi-note]
  (let [note-id (find-note-name midi-note)
        octave ((note-info note-id) :octave)
        offset (- octave 4)
        chord-key-note (+ midi-note (* offset 12 -1))
        chord-key-note-id (find-note-name chord-key-note)]
    (map #(+ % (* offset 12)) (map note (cons chord-key-note-id (chord-mappings chord-key-note-id))))))

(def fib [1 2 3 5 8 3 1 4 5 9 4 3 7 0 7 7 4 1 5 6 1 7 8 5 3 8 1 9 0 9 9 8 7 5 2 7 9 6 5 1 6 7 3 0 3 3 6 9 5 4 9 3 2 5 7 2 9 1 0 1])

(def bass
  (->>
   (phrase (cycle [9]) (flatten (conj fib [0 0 0])))
   (where :part (is :bass))
   (where :pitch (comp scale/lower scale/lower))))

(def chord_progression_slow
  (->>
   (phrase (cycle [3]) (flatten (map #(get-chord %) fib)))
   (where :part (is :melody))))

(def chord_progression_fast
  (->>
   (phrase (cycle [3/2 3/2 3/2 3/4 3/4 3/2 3/2 3/2 3/2]) (flatten (map #(map last (-> (choose [chord/ninth chord/seventh chord/triad]) (chord/root %))) fib)))
   (where :part (is :melody))))

(defmethod ll/play-note :bass [{hertz :pitch}] (darkbass hertz :attack 2 :release 9 :amp 0.1))
(defmethod ll/play-note :melody [{hertz :pitch dur :duration}] (short-tone hertz :dur dur :detune 0.2 :nharm 1 :amp 0.35))

(def track
  (->> bass
       (with chord_progression_slow)
       (with chord_progression_fast)
       (where :time (bpm 120))
       (where :duration (bpm 120))
       (where :pitch (comp temperament/equal scale/A scale/minor))))

(comment
  (ll/play track)
  (ll/stop)
  (recording-start "./Fibonacci in A minor.wav")
  (recording-stop)
)
