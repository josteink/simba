(ns simba.process
  (:use [clojure.java.shell :only (sh with-sh-dir)]
        [simba.io :as io]))

; ===========================================================
; global settings
; ===========================================================

(def patcher-dir "/home/jostein/fastbuild/autopatcher")
(def patcher-tool "./patch_rom_from_build.sh")


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
        retention         (:retention build 10)
        file-sets         (io/get-file-sets-from-config build)
        obsolete-sets     (get-sets-outside-retention file-sets retention)
        num-obsolete-sets (count obsolete-sets)]
    (io/output "Configuration has " (count file-sets) " file sets.")
    (io/output num-obsolete-sets " sets outside retention.")
    (doseq [[set-key file-set] obsolete-sets]
      (purge-file-set set-key file-set))))


; ===========================================================
; generating tablet UIs
; ===========================================================

(defn is-tabletui-patch? [name]
  (and (.contains name "tabletUI")
       (not (.contains name "md5sum"))))

(defn has-tabletui? [files]
  "check if list files has any element containing \"tabletUI\""
  (let [names (map #(.getName %) files)]
    (some is-tabletui-patch? names)))

(defn get-sets-without-tablet-ui [file-sets]
  (let [without-tabletui-keys (for [[k v] file-sets
                                    :when (not (has-tabletui? v))]
                                k)]
    (select-keys file-sets without-tabletui-keys)))

(defn get-rom-files [file-set]
  ;; keep it simple: files with extension .zip
  (filter #(= ".zip" (io/get-extension %)) file-set))

(defn generate-tablet-ui-for-file [file]
  (io/output "Processing file '" file "'")
  (let [filename      (.getName file)
        source-folder (-> file .getParentFile .getPath)
        result        (with-sh-dir patcher-dir
                        (sh patcher-tool source-folder filename))
        status        (:exit result)]
    (if (not (= 0 status))
      (do
        (io/output "Failed to generate tablet-ui:" (:out result)))
      (do
        (io/output "Successfully generated tablet-ui mod.")))))

(defn generate-tablet-ui-for [key file-set]
  (io/output "Generating tablet-ui for builds in file-set tagged '" key "'.")
  (let [rom-files (get-rom-files file-set)]
    (doseq [file rom-files]
      (generate-tablet-ui-for-file file))))

(defn generate-tablet-ui [build]
  (io/output "Generating tablet-ui for build-configuration '" (:config build) "'...")
  (let [
        file-sets          (io/get-file-sets-from-config build io/get-set-key-with-config)
        candidate-sets     (get-sets-without-tablet-ui file-sets)
        num-candidate-sets (count candidate-sets)]
    (io/output "Configuration has " (count file-sets) " file sets.")
    (io/output num-candidate-sets " sets without mods.")
    (doseq [[set-key file-set] candidate-sets]
      (generate-tablet-ui-for set-key file-set))))
