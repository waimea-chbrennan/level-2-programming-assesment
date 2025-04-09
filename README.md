# Old Gold Assesment Task

by Connor Brennan

---

This is a programming project for **NCEA Level 2**, assessed against standard [91896](as91896.pdf).

The project is written in the [Kotlin](https://kotlinlang.org) programming language and demonstrates a number of **advanced programming techniques**:
- Modifying data stored in collections
- Creating functions that use parameters and/or return values
- Using additional non-core libraries
---

## Project Files

- Program source code can be found in the [src](src/) folder
- Program test plan is in [test-plan.md](docs/test-plan.md) in the docs folder
- Program test evidence is in [test-results.md](docs/test-results.md) in the docs folder

---

## Project Description

This project is a kotlin implementation of the game 'old-gold'. The game is played on a 1d board with multiple coins, one of which is gold.
This is a two player game and the player who removes the coin from the board is the winner. A player can choose to remove a coin only if it is on the far left square of the board.
Otherwise, a player can move any coin any number of spaces to the left as long as a coin does not jump over any other coin or have more than one coin in a square.

