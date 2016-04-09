(ns lambdaphonic.a-picture-in-dorian-g
  (:use [overtone.live :refer :all]
        [leipzig.melody :refer :all]
        [lambdaphonic.live.synths]
        [lambdaphonic.live.constants :refer :all])
  (require [leipzig.scale :as scale]
           [leipzig.live :as ll]
           [leipzig.chord :as chord]
           [leipzig.temperament :as temperament]))

(defn to-base [input base]
  (map
    #(if (Character/isDigit %) (Integer/parseInt (str %)) (- (int (Character/toUpperCase %)) 55))
    (seq (.toString
           (biginteger
             (reduce str "" (map #(Integer/toString %) input))) base))))

(defn pi-to-base [base]
  (flatten (conj (to-base (rest pi) base) (to-base [(first pi)] base))))

(def pi-base-8 (to-base (rest pi) 8))
(def pi-base-12 (to-base (rest pi) 12))
(def pi-short (take 30 pi-base-8))

;; Source: https://www.goodreads.com/work/quotes/1858012-the-picture-of-dorian-gray
(defonce quotes
  ["The books that the world calls immoral are books that show the world its own shame."
   "You will always be fond of me. I represent to you all the sins you never had the courage to commit."
   "Experience is merely the name men gave to their mistakes."
   "Those who find ugly meanings in beautiful things are corrupt without being charming. This is a fault. Those who find beautiful meanings in beautiful things are the cultivated. For these there is hope. They are the elect to whom beautiful things mean only Beauty. There is no such thing as a moral or an immoral book. Books are well written, or badly written. That is all."
   "I don't want to be at the mercy of my emotions. I want to use them, to enjoy them, and to dominate them."
   "The only way to get rid of temptation is to yield to it."
   "To define is to limit."
   "There is only one thing in the world worse than being talked about, and that is not being talked about."
   "Nowadays people know the price of everything and the value of nothing."
   "I am too fond of reading books to care to write them."
   "When one is in love, one always begins by deceiving one's self, and one always ends by deceiving others. That is what the world calls a romance."
   "Children begin by loving their parents; as they grow older they judge them; sometimes they forgive them."
   "Behind every exquisite thing that existed, there was something tragic."
   "Humanity takes itself too seriously. It is the world's original sin. If the cave-man had known how to laugh, History would have been different."
   "Nowadays most people die of a sort of creeping common sense, and discover when it is too late that the only things one never regrets are one's mistakes."
   "Nothing can cure the soul but the senses, just as nothing can cure the senses but the soul."
   "Words! Mere words! How terrible they were! How clear, and vivid, and cruel! One could not escape from them. And yet what a subtle magic there was in them! They seemed to be able to give a plastic form to formless things, and to have a music of their own as sweet as that of viol or of lute. Mere words! Was there anything so real as words?"
   "Live! Live the wonderful life that is in you! Let nothing be lost upon you. Be always searching for new sensations. Be afraid of nothing."
   "Some things are more precious because they don't last long."
   "Laughter is not at all a bad beginning for a friendship, and it is by far the best ending for one."
   "Every portrait that is painted with feeling is a portrait of the artist, not of the sitter."
   "Whenever a man does a thoroughly stupid thing, it is always from the noblest motives."
   "The world is changed because you are made of ivory and gold. The curves of your lips rewrite history."
   "There is no such thing as a moral or an immoral book. Books are well written, or badly written. That is all."
   "You must have a cigarette. A cigarette is the perfect type of a perfect pleasure. It is exquisite, and it leaves one unsatisfied. What more can one want?"
   "The basis of optimism is sheer terror."
   "I have grown to love secrecy. It seems to be the one thing that can make modern life mysterious or marvelous to us. The commonest thing is delightful if only one hides it."
   "I love acting. It is so much more real than life."
   "What does it profit a man if he gain the whole world and lose his own soul?"
   ])

(defonce quote-buffers (map #(speech-buffer % :voice :whisper) quotes))
(defonce max-quote-duration (apply max (map #(:duration (buffer-info %)) quote-buffers)))

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
    (phrase (cycle[_8th]) pi-base-12)
    (where :part (is :melody))))

(def melody2
  (->>
    (phrase (cycle[_8th]) pi-base-8)
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

(def theblips
  (->>
    (phrase (cycle [(/ _8th 1)]) pi)
    (where :part (is :blips))))

(def track
  (->> melody
       (with melody2)
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

(defonce narrator-trigger-id (trig-id))
(defsynth narrator-trigger [freq (/ 1 max-quote-duration) id narrator-trigger-id]
  (let [t (impulse:kr freq)
        v (demand:kr t 0 (dseq (map #(buffer-id %) quote-buffers) INF))]
    (send-trig:kr :in t :id id :value v)))

(definst narrator [buf-id (buffer-id (first quote-buffers)) amp 1]
  (let [sig (play-buf:ar 1 buf-id :rate 0.5)
        dur (buf-dur:kr buf-id)
        env (env-gen (perc :attack (* 0.5 dur) :release (* 2.25 dur)) :action FREE)]
    (mul (free-verb2 sig sig) env amp)))

(comment
  (recording-start "./The picture of Dorian Pi.wav")
  (def nt (narrator-trigger 1/12))
  (ctl nt :freq 1/12)
  (on-trigger narrator-trigger-id #(narrator % :pan :amp 0.5) :narrator)
  (remove-event-handler :narrator)
  (kill nt)
  (inst-fx! narrator fx-freeverb)
  (clear-fx narrator)
  (volume 20)
  (def b (blips :amp 0.35))
  (ctl b :amp 0.15)
  (ll/play track)
  (ll/stop)
  (recording-stop)
  (stop)
)
