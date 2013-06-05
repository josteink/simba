(ns simba.io
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

(defn get-set-key-with-config [fs-object]
  "simply check if name contains \"linaro\" and append that to key if it does"
  (let [default-key (get-set-key fs-object)
        file-name (.getName fs-object)
        linaro (re-find #"linaro" file-name)]
    (if linaro
      (str default-key "-linaro")
      default-key)))

(defn- get-set-from-key [file-sets key]
  (if (contains? file-sets key)
    (get file-sets key)
    '()))

(defn- add-file-to-set [new-file existing-set]
  (if (.isDirectory new-file)
    existing-set
    (cons new-file existing-set)))

(defn get-file-sets
  ([files key-name-func] (get-file-sets files key-name-func {}))
  ([files key-name-func existing-sets]
     (if (empty? files)
       existing-sets
       (let [current-file    (first files)
             remaining-files (rest files)
             set-key         (key-name-func current-file)
             file-set        (get-set-from-key existing-sets set-key)
             new-file-set    (add-file-to-set current-file file-set)
             new-sets        (assoc existing-sets set-key new-file-set)]
         (recur remaining-files key-name-func new-sets)))))

(defn get-extension [file]
  (let [filename   (.getName file)
        lastDotPos (.lastIndexOf filename ".")]
    (if (= -1 lastDotPos)
      "" ;; no extension
      (.substring filename lastDotPos))))

(defn get-file-sets-from-config
  "Returns a file-set for the build provided"
  ([build] (get-file-sets-from-config build get-set-key))
  ([build key-name-func]
     (let [path              (:path build)
           files             (get-files path)
           file-sets         (get-file-sets files key-name-func)]
       file-sets)))
