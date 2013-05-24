(ns simba.process
  (:use [simba.io :as io]))

; ===========================================================
; main processing logic
; ===========================================================

(defn process-build [build]
  "Attempts to process a single build-defintion"
  ;; TODO: add error-handling
  (io/output "Processing build-configuration '" (:config build) "'...")
  (let [processes (:processes build [])]
    (io/output "Configuration has " (count processes) " sub-processess.")
    (doseq [process processes]
      (process build))))

(defn process-builds [builds]
  "Attempts to process all build-definitions"
  ;; TODO: add error-handling
  (io/output "Processing " (count builds) " configurations...")
  (doseq [build builds]
    (process-build build)))


; ===========================================================
; purging old builds
; ===========================================================

(defn- get-sets-outside-retention [file-sets retention]
  (let [
        sorted-keys   (reverse (sort (keys file-sets)))
        obsolete-keys (drop retention sorted-keys)]
    (select-keys file-sets obsolete-keys)))

(defn- purge-file-set [key files]
  (io/output "Purging file set '" key "' with " (count files) " files.")
  (doseq [file files]
    ;;(io/output "Purging file " (.getName file))
    (.delete file)))

(defn purge-files [build]
  (io/output "Purging files for build-configuration '" (:config build) "'...")
  (let [
        path              (:path build)
        retention         (:retention build 10)
        files             (io/get-files path)
        file-sets         (io/get-file-sets files)
        obsolete-sets     (get-sets-outside-retention file-sets retention)
        num-obsolete-sets (count obsolete-sets)]
    (io/output "Configuration has " (count files) " files and " (count file-sets) " file sets.")
    (io/output num-obsolete-sets " sets outside retention.")
    (doseq [[set-key file-set] obsolete-sets]
      (purge-file-set set-key file-set))))


; ===========================================================
; generating tablet UIs
; ===========================================================

(defn has-tabletui? [files]
  "check if list files has any element containing \"tabletUI\""
  (let [names (map #(.getName %) files)]
    (some #(.contains % "tabletUI") names)))

(defn get-sets-without-tablet-ui [file-sets]
  (let [without-tabletui-keys (for [[k v] file-sets
                                    :when (not (has-tabletui? v))]
                                k)]
    (select-keys file-sets without-tabletui-keys)))

(defn get-rom-files [file-set]

  )

(defn generate-tablet-ui-for [key file-set]
  ;; TODO: implement
  ;; TODO: lookup shell invocation from lein-drip code
  ;; 1. identify actual ZIP files to process (1-many)
  ;; 2. generate proper names for patches (on a per orig-ZIP basis)
  ;; for all, then....
  ;; 3. copy ZIP to local storage
  ;; 4. invoke (shell script for single zip)
  ;; 5. copy result to target
  )

(defn generate-tablet-ui [build]
  (io/output "Generating tablet-ui for build-configuration '" (:config build) "'...")
  (let [
        path               (:path build)
        files              (io/get-files path)
        file-sets          (io/get-file-sets files)
        candidate-sets     (get-sets-without-tablet-ui file-sets)
        num-candidate-sets (count candidate-sets)]
    (io/output "Configuration has " (count files) " files and " (count file-sets) " file sets.")
    (io/output num-candidate-sets " sets without mods.")
    (doseq [[set-key file-set] candidate-sets]
      (generate-tablet-ui-for set-key file-set))))
