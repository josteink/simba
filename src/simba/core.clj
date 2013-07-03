(ns simba.core
  (:use [simba.process :as p]
        [simba.io :as io])
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
              :processes [p/purge-files p/generate-tablet-ui p/update-index]
              }
             {
              :config "tf101g-4.2" ;; just for show really
              :path "/mnt/stw/managed/tf101g/cm10.1/"
              :retention 10
              :processes [p/purge-files p/generate-tablet-ui p/update-index]
              }
             ])

(defn- set-test-path [build-config]
  (let [
        path     (:path build-config)
        new-path (clojure.string/replace path #"/mnt" "/tmp")]
    (assoc build-config :path new-path)))

(def test-builds (map set-test-path builds))

(defn -main
  "Runs the main agent-loop, handling all build definitions"
  [& args]
  ;; todo: add timer-loop and execute process-builds via timer-loop
  (if (=  0 (count args))
    (do
      (io/output "Processing builds...")
      (p/process-builds builds))
    (do
      (io/output "Processing test-builds...")
      (p/process-builds test-builds))))
