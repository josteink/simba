(ns nexfilesd.io
  (:import (java.util Date)
           (java.text DateFormat)
           (java.io File)))

(defn output [& text]
  (let [formatter (DateFormat/getDateTimeInstance)
        now (Date.)]
    (print (str (.format formatter now) ": ")))
  (println (apply str text)))

(defn- fs-object [path]
  (File. path))

(defn get-files [path]
  (let [path-object (fs-object path)]
    (.listFiles path-object)))

(defn- get-set-key [fs-object]
  (let [file-name (.getName fs-object)]
    ;; first continious block of 8 numbers
    (first (re-seq #"[0-9]{8}" file-name))))

(defn- get-set-from-key [file-sets key]
  (if (contains? file-sets key)
    (get file-sets key)
    '()))

(defn- add-file-to-set [new-file existing-set]
  (if (.isDirectory new-file)
    existing-set
    (cons new-file existing-set)))

(defn get-file-sets
  ([files] (get-file-sets files {}))
  ([files existing-sets]
     (if (empty? files)
       existing-sets
       (let [current-file    (first files)
             remaining-files (rest files)
             set-key         (get-set-key current-file)
             file-set        (get-set-from-key existing-sets set-key)
             new-file-set    (add-file-to-set current-file file-set)
             new-sets        (assoc existing-sets set-key new-file-set)]
         (recur remaining-files new-sets)))))

(defn describe-file-set [file-set])
