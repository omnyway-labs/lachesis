(ns lachesis.local-store-test
  (:require [clojure.test :refer :all]
            [lachesis.core :as l]))

(defn init-lachesis [f]
  (l/init!)
  (f)
  (reset! l/store nil)
  (reset! l/*settings* nil))

(use-fixtures :each init-lachesis)

(deftest basic-creation-realization
  (let [test-key "test-key"]
    (l/identified-promise test-key)
    (l/deliver-by-id test-key {:one-test :item})
    (is (= {:one-test :item} (l/deref-by-id test-key 5000)))))


;; This isn't particularly necessary on a single node, but I think this
;; is an important invariant for any other storage methods to satisfy,
;; to ensure we can coordinate asynchronously.
(deftest out-of-order-still-resolves
  (let [test-key "test-key"
        off-thread-deref (future (l/deref-by-id test-key 5000))]
    (l/identified-promise test-key)
    (l/deliver-by-id test-key {:second-test :test-item})
    (is (= {:second-test :test-item} @off-thread-deref))))
