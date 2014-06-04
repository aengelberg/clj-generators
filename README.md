# clj-generators

My all-time favorite feature of Python is "generators." It allows you to write lazy sequences imperatively.

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
	
This feature is so amazing and powerful, a common practice of mine is to ponder how to implement such
magic in other languages. Python implements this feature using virtual stacks to suspend the computation
in memory. Here is an implementation of generators in Clojure, using core.async's "go" blocks. The
infinite_range example above would translate to the following Clojure code:

	(defn infinite-range []
	  (generator
	    (loop [x 1]
		  (yield x)
		  (recur (inc x)))))
	
	(take 5 (infinite-range))
	=> (1 2 3 4 5)

## Usage

Add the following to your dependencies:

	(no lein dependency yet)

To make a generator in Clojure, simply use the `generator` macro, and insert instances of the `yield` macro
within the generator.

	(generator
	  (yield 1) ; return 1
	  (yield 2 3)) ; return 2 and then 3
	=> (1 2 3)
	
	(take 3 (generator (yield 1 2 3 4 5)
	                   (println "still computing"))) ; generators will only compute when values are requested
	=> (0 1 2)

What is essentially happening behind the scenes is that the generator spawns a go block
to compute the values and write them to a chan, and then it returns a lazy sequence that reads from the chan.

### Pros
- Common macros like `loop` previously weren't able to construct lazy sequences, but now they can
- Clojure noobs who can only write imperative code (you know who you are) can now write their own lazy sequences

### Cons
- Uses up to 2 threads if available
- Much slower than their genuine lazy-sequence counterparts
- More of a proof-of-concept than an actual library

## License

Copyright © 2014

Distributed under the Eclipse Public License, the same as Clojure.
