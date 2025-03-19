# Plan for Testing the Program

The test plan lays out the actions and data I will use to test the functionality of my program.

Terminology:

- **VALID** data values are those that the program expects
- **BOUNDARY** data values are at the limits of the valid range
- **INVALID** data values are those that the program should reject

---

## Testing: Coin Number to move **(VALID)**
The program has to get input on each player's turn on what coin they want to move. They should do this by pressing the arrow keys or WASD to move a cursor left or right to select the coin and then press enter to select the coin.
### Test Data To Use
Use arrow keys to navigate *directly* to a valid coin and press enter. This **will not** involve selecting an invalid coin or navigating off the board. 
eg RIGHT, RIGHT, ENTER to select a coin at index 2 starting at index 0.
### Expected Test Result
The program should store the selected coin and continue to get the player's move.

---

## Testing: Coin Number to move **(BOUNDARY)**
The program has to get input on each player's turn on what coin they want to move. They should do this by pressing the arrow keys or WASD to move a cursor left or right to select the coin and then press enter to select the coin.
### Test Data To Use
We will use arrow keys to select a coin *on the very edges of the board*.
eg ENTER or RIGHT x8, ENTER
### Expected Test Result
The program should store the selected coin and continue to get the player's move.

---

## Testing: Coin Number to move **(INVALID)**
The program has to get input on each player's turn on what coin they want to move. They should do this by pressing the arrow keys or WASD to move a cursor left or right to select the coin and then press enter to select the coin.
### Test Data To Use
We will use **keys other than arrow and WASD and enter**.
eg XZC(*&CJHVK
### Expected Test Result
We should be able to hammer on the keyboard and the program should reject all irrelevant key presses with no changes to game state.
---
