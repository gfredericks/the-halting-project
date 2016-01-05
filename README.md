# The Halting Project

What's the simplest Turing Machine with unknown behavior?

## What?

The
[undecidability of The Halting Problem](https://en.wikipedia.org/wiki/Halting_problem#Sketch_of_proof)
implies that we can't write a program that can analyze any program to
determine if it ever terminates. That doesn't stop us from analyzing
individual programs using ad-hoc methods (some programs obviously
terminate or obviously don't), but ad-hoc methods will hit a wall
somewhere. I'm interested in finding that wall.

## Why?

I thought it would be fun.

## Is this a reasonable goal?

I'm not sure yet.

## What's do you consider a Turing Machine to be exactly for these purposes?

That's a good question.

It seemed useful to choose a definition that simplifies this project.

A turing machine operates on an infinite two-way binary tape, where
each square of the tape can only be `0` or `1`.

The tape is always initialized with all `0`s (there is no notion of
the "input" to a program).

A machine has `N` states (excluding the halt state, which isn't a
proper state), numbered `0` to `N-1` (state `0` is the start state and
state `N` is the halt state).

Each state specifies the machine's behavior based on the value read
from the current position in the tape. E.g., `1L1` means "write a `1`,
move left, and transition to state `1`.

An example:

| State | Read 0 | Read 1 |
|------:|:-------|:-------|
|      0|1L1     |1R2     |
|      1|1L2     |1L1     |
|      2|1L3     |0R4     |
|      3|1R0     |1R3     |
|      4|1L5     |OR0     |

### Would a different definition be better?

I think the above definition could be refined to exclude some
redundancy, but then counting would be harder. I dunno.

## Related

[The Busy Beaver Game](https://en.wikipedia.org/wiki/Busy_beaver).

## License

Copyright © 2016 Gary Fredericks

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
