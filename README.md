# Lachesis, a library for asynchronous promises.

## The Problem: Coordinating across cues indirectly is difficult

In most cases, we we don't have this issue, as we can simply coordinate cues
by calling them. Since cue calls block, we can fetch data from multiple sources
without issues.

If we have to coordinate an **asynchronous** event from a third party, we can't
rely on stateless cues. We could coordinate using database storage, but that's
a specific solution to a more general problem.

## A Solution: Asynchronous, named promises

One way for two cues to coordinate is to both know the identifier for a given
promise. This assumes that they're both attempting to work with the same data.
We advise using either Orphids or the IDs of outside events to avoid collision,
but this buys us a few things:

### Ordering doesn't matter as much

One part of the system can technically attempt to dereference a promise that
hasn't been instantiated by the cue that intends to deliver it. The behaviour is
the same in that the dereferencer blocks for a given amount of time before realizing
the promise value. This has almost no value if the two cues co-reside on a machine,
but can be useful in the event of distributed coordination.

### The API

``` clojure
;; This provides a dereffabble (in case we want to pass it directly, and registers
;; the promise via the chosen storage mechanism
(identified-promise [id])

;; This allows delivery by id, fetching the promise from storage and providing it
(deliver-by-id id v)

;; This allows deref by id
(deref-by-id id timeout)

Works the same way as a promise deref
```
