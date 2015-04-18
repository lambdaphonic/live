(ns lambdaphonic.live
  (:use [overtone.live :refer :all]
        [leipzig.melody :refer :all]
        [lambdaphonic.live.synths])
  (require [leipzig.scale :as scale]
           [leipzig.live :as ll]
           [leipzig.chord :as chord]
           [leipzig.temperament :as temperament]))


