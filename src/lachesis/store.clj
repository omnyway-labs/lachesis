(ns lachesis.store)

(defprotocol NamedPromiseStore
  (register! [_ id])
  (lookup [_ id])
  (remove! [_ id]))
