# Alphabet Match Game

A console-based tile matching game developed for the CME1212 Algorithms and Programming II course.

The project demonstrates custom stack and queue implementations through a game built around five tile sets, a reserve queue, and a supplementary queue.

## Features

- Five randomly generated tile sets
- `Match(i,j)`, `AddSet(i)`, `ShiftQueue`, and `F` commands
- Automatic tile addition after every three valid steps
- Score, step, and shift tracking
- Top 10 high-score table stored in `HighScoreTable.txt`
- Custom `Stack` and `Queue` classes

## Run

Compile the source files:

```bash
javac Stack.java Queue.java Test.java
```

Start the game:

```bash
java Test
```

## Course

CME1212 - Algorithms and Programming II, Homework 1
