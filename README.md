# clj-generators

Proof of concept: writing lazy sequences imperatively. Based on the Python "generator / yield" feature, but uses Clojure's `go` blocks.

```python
def infinite_range():
	x = 1
	while True:
		yield x
		x += 1
for i in infinite_range():
	if (i > 5):
		break
	else:
		print(i)
=> 1 2 3 4 5
```

```clojure
(defn infinite-range []
  (generator
    (loop [x 1]
	  (yield x)
	  (recur (inc x)))))

(take 5 (infinite-range))
=> (1 2 3 4 5)
```

## Usage

Add the following to your dependencies:

	[clj-generators "0.1.0-SNAPSHOT"]

To make a generator in Clojure, simply use the `generator` macro, and insert instances of the `yield` macro
within the generator.

```clojure
(use 'clj-generators.core)
(generator
  (yield 1) ; return 1
  (yield 2 3)) ; return 2 and then 3
=> (1 2 3)

(take 3 (generator (yield 1 2 3 4 5)
                   (println "still computing"))) ; generators will only compute when values are requested
=> (1 2 3)
```

## License

Copyright © 2014

Distributed under the Eclipse Public License, the same as Clojure.
