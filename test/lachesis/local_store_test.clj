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
