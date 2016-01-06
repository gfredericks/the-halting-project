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

An example (which only halts when reading a `0` in state `4`):

| State | Read 0 | Read 1 |
|------:|:-------|:-------|
|      0|1L1     |1R2     |
|      1|1L2     |1L1     |
|      2|1L3     |0R4     |
|      3|1R0     |1R3     |
|      4|1L5     |0R0     |

We can identify turing machines with natural numbers by first ordering
them by size and then by the natural lexicographic ordering of the
above representation. So machine `#0` is the empty turing machine (which
has already halted), machine `#1` is:

| State | Read 0 | Read 1 |
|------:|:-------|:-------|
|      0|0L0     |0L0     |

machine `#2` is:

| State | Read 0 | Read 1 |
|------:|:-------|:-------|
|      0|0L0     |0L1     |

and so on or something.

### Would a different definition be better?

I think the above definition could be refined to exclude some
redundancy, but then counting would be harder. I dunno.

## Related

[The Busy Beaver Game](https://en.wikipedia.org/wiki/Busy_beaver).

## Results So Far

Here I'll list different techniques for proving halts-or-not, in order
of the smallest turing machine that requires that technique.

### By Definition

Machine `#0` halts by definition, so there's nothing further to prove
about it.

### Immediate Loop

Machine `#1` cannot halt because its first transition is back to state
`0` and so it will always read `0`s and is trivially in an infinite
loop.

| State | Read 0 | Read 1 |
|------:|:-------|:-------|
|      0|0L0     |0L0     |

### Uncanonical

Machine `#4` can be ignored, since it is equivalent to machine `#2`
(which, after reading a 1, moves left before halting instead of
right).

| State | Read 0 | Read 1 |
|------:|:-------|:-------|
|      0|0L0     |0R1     |


### Halts Quickly

Machine `#9` halts after 1 step, which can be verified by simulating
it.

| State | Read 0 | Read 1 |
|------:|:-------|:-------|
|      0|0L1     |0L0     |

### Unreachable Halt State

Machine `#1793` cannot halt because there is no transition path from
its start state (`0`) to its halt state (`2`).

| State | Read 0 | Read 1 |
|------:|:-------|:-------|
|      0|0L1     |0L0     |
|      1|0L0     |0L0     |

### Onelessness

Machine `#1795` cannot halt because it only halts when reading a `1`,
but it never writes a `1`.

| State | Read 0 | Read 1 |
|------:|:-------|:-------|
|      0|0L1     |0L0     |
|      1|0L0     |0L2     |

### Unidirectionality

Machine `#1867` should not halt because it is show by simulation to
not halt within `N` steps, and its transition table shows that it
always moves in the same direction when reading a `0`, so it will only
keep reading `0`s.

| State | Read 0 | Read 1 |
|------:|:-------|:-------|
|      0|0L1     |0L0     |
|      1|1L0     |0L2     |

### Unknown

Machine `#2189` presumably does not halt, but I haven't come up with
a general proof for it yet.

| State | Read 0 | Read 1 |
|------:|:-------|:-------|
|      0|0L1     |0L2     |
|      1|1R0     |0L0     |

## License

Copyright © 2016 Gary Fredericks

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
