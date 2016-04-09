(ns lambdaphonic.playground
  (use [overtone.live :refer :all]
       [lambdaphonic.overtone.helpers])
  (require [shadertone.tone :as t]))

(comment
(t/start-fullscreen "resources/daily_1_3_15.glsl" :textures [:overtone-audio])
(t/start-fullscreen "resources/ether.glsl" :textures [:overtone-audio])
(t/start-fullscreen "resources/gameboy.glsl" :textures [:overtone-audio])
(t/start-fullscreen "resources/inputsound.glsl" :textures [:overtone-audio])
(t/start-fullscreen "resources/io.glsl" :textures [:overtone-audio])
(t/start-fullscreen "resources/volumetric_lines.glsl" :textures [:overtone-audio])
(t/stop)
)


(do
  (def moria1 [:E4  :C4 :A3 :C4 :E4  :C4 :A3 :C4 :E4  :C4 :A3  :C4  :F4 :E4 :D4 :C4])
  (def moria2 [:D4  :B3 :G3 :B3 :D4  :B3 :G3 :B3 :D4  :B3 :G3  :B3  :E4 :D4 :C4 :B3])
  (def moria3 [:B3  :C4 :D4 :F4 :E4  :D4 :C4 :A3 :B3  :E3 :F#3 :G#3 :A3 :A3 :A3 :A3])
  (def moria4 [:A3  :C4 :E4 :C4 :A3  :C4 :E4 :C4 :A3  :C4 :E4  :F4  :E4 :D4 :C4 :B3])
  (def moria5 [:G#3 :B3 :D4 :B3 :G#3 :B3 :D4 :B3 :G#3 :B3 :D4  :F4  :E4 :D4 :C4 :B3])
  (def moria6 [:A3  :C4 :E4 :C4 :A3  :C4 :E4 :C4 :A3  :C4 :E4  :G4  :F4 :E4 :D4 :C4])
  (def moria7 [:B3  :C4 :D4 :F4 :E4  :D4 :C4 :A3 :B3  :E3 :F#3 :G#3 :A3 :A3 :A3 :A3])

  (def bmoria [:E2 :E2 :D2 :D2 :C2 :C2 :B1 :B1 :E2 :E2 :A1 :B1 :E2 :E2 :B1 :B1 :C2 :C2 :A1 :B1])
  (def bass-line-moria (atom bmoria))

  (def moria (atom  [moria1 moria2 moria1 moria2 moria1 moria3 moria4 moria5 moria6 moria7]))
  (def moria-current-part (atom (first @moria))))


(defsynth foo [freq 440 out-bus 0]
  (let [sig (saw [freq (+ freq 1)])
        sig (lpf sig 2000)
        env (env-gen (perc 0.1 0.25) :action FREE)]
    (out out-bus (* sig env))))


(defn melody [metro t beat]
  (at t
    (let [next-beat (+ beat 1)
          next-t (+ t (mspb metro))]
      (dorun
        (map (fn [b n]
                (at (+ t (mspb metro (+ 1 b)))
                    (do
                      (foo (midi->hz (note n))))))
              (range 0 1 1/4)
              (take 4 @moria-current-part)))
      (apply-by next-t #'melody [metro next-t next-beat]))))

(comment
(def metro (metronome 120))
(melody metro (atbeat metro 1) 0)
(volume 0.5)

)
