# Ultimate Tic Tac Toe

A Java implementation of Ultimate Tic Tac Toe featuring a Monte Carlo Tree Search AI opponent and a built-in testing framework.

---

## What is Ultimate Tic Tac Toe?

Ultimate Tic Tac Toe is a strategic variant of the classic game. The board is a 3×3 grid of 9 smaller tic tac toe boards. Players must win three small boards in a row to win the game — but the twist is that wherever you play within a small board, you **send your opponent to the corresponding board** on the global grid. Every move is both a local play and a global decision.

For example, if you play in the top-right cell of any small board, your opponent must play their next move inside the top-right small board. If the target board is already won or full, your opponent gets a free choice.

---

## Features

- Full rule implementation including the "sending" mechanic and free-choice fallback
- Human vs Human, Human vs AI, and AI vs AI modes
- Monte Carlo Tree Search (MCTS) AI with tunable iteration count
- Built-in `GameTester` class for running batches of games and comparing AI configurations
- Console board display showing won boards, drawn boards, and the active forced board

---

## Project Structure

| File | Responsibility |
|---|---|
| `Main.java` | Game loop, move validation, win detection, board printing |
| `Board.java` | Holds the 9 small boards |
| `SmallBoard.java` | Holds 9 cells, tracks if full |
| `Cell.java` | Enum: X, O, EMPTY |
| `Move.java` | Represents a move — which small board and which cell |
| `Player.java` | Interface implemented by both HumanPlayer and AIPlayer |
| `HumanPlayer.java` | Reads moves from console input, respects forced board |
| `AIPlayer.java` | MCTS-based AI player |
| `MCTSNode.java` | MCTS tree node — handles selection, expansion, backpropagation |
| `State.java` | Self-contained game state used by MCTS for simulation |
| `GameTester.java` | Testing framework for watching and benchmarking AI games |

---

## How to Run

**Play the game:**
```
javac *.java
java Main
```

By default both players are human. To enable the AI, edit `Main.java`:
```java
Player playerO = new AIPlayer(Cell.O, 10000); // 10,000 iterations per move
```

**Run the tester:**
```
java GameTester
```

This opens a menu with three options:
1. Watch a single AI vs AI game move by move
2. Run a batch of silent games and see win/draw statistics
3. Compare two AIs with different iteration counts

---

## The AI — Monte Carlo Tree Search

The AI uses MCTS, which works by repeatedly simulating random games from the current position and using the results to build up a statistical picture of which moves are most promising. It does not use a handcrafted evaluation function — the signal comes entirely from game outcomes.

Each iteration runs four phases:

1. **Selection** — walk down the existing tree using the UCB1 formula to balance exploration and exploitation
2. **Expansion** — add one new child node for an untried move
3. **Simulation** — play the game out randomly from the new node to completion
4. **Backpropagation** — update win/visit counts back up the tree

The final move is chosen by picking the child of the root with the most visits — a more robust signal than raw win rate.

**Tuning the AI:** The iteration count is the main dial for strength vs speed. Higher values produce stronger play at the cost of thinking time. A good starting point is 10,000 iterations, which typically runs in under a second on modern hardware.

---

## Testing

`GameTester` provides three modes:

**Watch a game** — prints the full board state after every AI move, including which board was played in and how long the AI took to think.

**Batch mode** — runs N games silently and reports win/draw percentages and average game time. Useful for checking whether the AI is balanced between X and O.

**Comparison mode** — runs a batch with different iteration counts for X and O. Use this to measure whether more iterations actually translate to stronger play.

Example output:
```
--- Batch: 20 games  |  X=10000 itr  O=1000 itr ---

Game  1: X  (1243ms)
Game  2: X  (1187ms)
Game  3: O  (1205ms)
...

--- Summary ---
X wins : 14 (70.0%)
O wins : 4  (20.0%)
Draws  : 2  (10.0%)
Avg game time: 1215ms
```