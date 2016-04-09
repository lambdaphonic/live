(ns lambdaphonic.live
  (use [overtone.live :refer :all]
       [lambdaphonic.overtone.helpers]
       [lambdaphonic.overtone.synths]))

(comment
(ns lambdaphonic.live
  (:use [overtone.live :refer :all]
        [lambdaphonic.live.constants]
        [mud.core :refer :all]
        [mud.chords :refer :all]
        [lambdaphonic.live.synths])
  (require [mud.timing :as time]))

(comment
(def lorem "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris. Maecenas congue ligula ac quam viverra nec consectetur ante hendrerit. Donec et mollis dolor. Praesent et diam eget libero egestas mattis sit amet vitae augue. Nam tincidunt congue enim, ut porta lorem lacinia consectetur. Donec ut libero sed arcu vehicula ultricies a non tortor. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean ut gravida lorem. Ut turpis felis, pulvinar a semper sed, adipiscing id dolor. Pellentesque auctor nisi id magna consequat sagittis. Curabitur dapibus enim sit amet elit pharetra tincidunt feugiat nisl imperdiet. Ut convallis libero in urna ultrices accumsan. Donec sed odio eros. Donec viverra mi quis quam pulvinar at malesuada arcu rhoncus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. In rutrum accumsan ultricies. Mauris vitae nisi at sem facilisis semper ac in est.")
(def short-lorem "Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
(def short-lorem-buf (speech-buffer short-lorem :voice :whisper))
(def lorem-buf (speech-buffer lorem :voice :whisper))

(def s1 (lorem-buf :rate 1 :out-bus 0 :loop? true))

(defsynth l [dur 10 in-bus 2 out-bus 0]
 (let [env (env-gen (perc 0.7 dur) :action FREE)
       sig (free-verb (in:ar in-bus))]
  (out:ar out-bus (* env sig))))
(def f (l 100))
(kill s1)
(ctl s1 :out-bus 2)
(stop)
  )
  )

