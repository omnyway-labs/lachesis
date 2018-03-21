(ns lachesis.core
  (:require [lachesis.store :as store]
            [lachesis.store.local :as local-store])
  (:refer-clojure :exclude [deref deliver]))

(def default-settings {:ttl (* 30 60 1000)
                       :deref-timeout 10000
                       :store-type :local})

(def ^:dynamic *settings* (atom nil))

(defonce store (atom nil))

(defn setting [k]
  (get @*settings* k))

(defmacro with-settings [settings & body]
  `(binding [*settings* (atom ~settings)]
     ~@body))

(defn build-store [store-type]
  (case store-type
    :local (local-store/make-store @*settings*)))

;; Public Interfaces

(defn init!
  ([]
   (init! {}))
  ([config]
   (when-not @*settings*
     (reset! *settings* (merge default-settings config)))
   (reset! store (build-store (setting :store-type)))))

;; Provides a reference to the promise we'd like to deref
(defn named-promise [id]
  (when-not @store
    (throw (ex-info "Lachesis has not been initialized"
                    {})))
  (store/register! @store id))

;; Looks up and attempts to deref that promise
(defn deref
  ([id]
   (deref id (setting :deref-timeout)))
  ([id timeout]
   (let [p (store/lookup @store id)]
     (clojure.core/deref p timeout nil))))

(defn deliver [id v]
  (let [p (store/lookup @store id)]
    (when-not (realized? p)
      (clojure.core/deliver p v))))
