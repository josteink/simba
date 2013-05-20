(ns nexfilesd.core
  (:use [nexfilesd.process :as p] [nexfilesd.io :as io])
  (:gen-class))

(def builds [{
              :config "tf101-4.1" ;; just for show really
              :path "/mnt/stw/managed/tf101/cm10/"
              :retention 10
              :processes [p/purge-files]
              }
             {
              :config "tf101-4.2" ;; just for show really
              :path "/mnt/stw/managed/tf101/cm10.1/"
              :retention 10
              :processes [p/purge-files p/generate-tablet-ui]
              }
             {
              :config "tf101g-4.2" ;; just for show really
              :path "/mnt/stw/managed/tf101g/cm10.1/"
              :retention 10
              :processes [p/purge-files p/generate-tablet-ui]
              }
             ])

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; todo: add timer-loop and execute process-builds via timer-loop
  (io/output "Processing builds...")
  (p/process-builds builds))
