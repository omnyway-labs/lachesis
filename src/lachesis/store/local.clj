(ns lachesis.store.local
  (:require [lachesis.store :as store]
            [clojure.core.cache :as cache]))

(defrecord LocalPromiseStore [store]
  store/NamedPromiseStore
  (register! [_ id]
    (LocalPromiseStore. (swap! store
                               cache/through-cache
                               id
                               (fn [x] (promise)))))
  (remove! [_ id]
    (LocalPromiseStore. (swap! store
                               cache/evict
                               id)))
  (lookup [_ id]
    (get @store id)))

(defn make-store [{:keys [ttl] :as config}]
  (LocalPromiseStore. (atom (cache/ttl-cache-factory {} :ttl ttl))))
