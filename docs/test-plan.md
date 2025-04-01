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
We will use **keys other than arrow and WASD and enter/space**.
eg XZC(*&CJHVK
### Expected Test Result
We should be able to hammer on the keyboard and the program should reject all irrelevant key presses with no changes to game state.

---

## Testing: Board initialization
The board should generate number of gold coins according to one gold coin, NUM_SILVER_COINS and NUM_BOARD_SPACES compile time constants and generate a board size that is the sum of
all of these. We should not generate the board with a gold coin in board index 0 as the player who starts would instantly win. The board should be generated randomly.

### Test Data To Use

Run the game multiple times and observe the result of the generated board.

### Expected Test Result
Create and display a randomly generated board that has 1 gold coin, NUM_SILVER_COINS other coins and
NUM_BOARD_SPACES spaces in the board. There should only be one coin per cell and the gold coin should now spawn in the 0th index.

---

## Testing: Moving A Coin (invalid)

The user should only be able to move the cursor to the left of the coin they have selected that they want to move.
This is to prevent the user moving a coin to the right initially as it is a clear indication that they can not do this.

### Test Data To Use

We can select a coin and then try to move the cursor right eg from index 0:
D D ENTER DDDDDDDDDDDDD RIGHT RIGHT A

### Expected Test Result
The cursor should move right two spaces, the coin should be selected, then the cursor will not move right no matter how many times the user presses right,
then the cursor will move left one space.

---

## Testing:

### Test Data To Use

### Expected Test Result

---

## Testing:

### Test Data To Use

### Expected Test Result

---

## Testing:

### Test Data To Use

### Expected Test Result

---

## Testing:

### Test Data To Use

### Expected Test Result

---

## Testing:

### Test Data To Use

### Expected Test Result

---
