# Plan for Testing the Program

The test plan lays out the actions and data I will use to test the functionality of my program.

Terminology:

- **VALID** data values are those that the program expects
- **BOUNDARY** data values are at the limits of the valid range
- **INVALID** data values are those that the program should reject

There are many important aspects to this program that have to be tested to ensure and prove proper handling of valid, boundary and invalid cases.

We will test the entire program, especially with invalid inputs to ensure that it can cope appropriately with these.


List of all possible program inputs for reference:
 - Player name input
 - left, right (selecting a coin)
 - enter (selecting a coin)
 - left, right (moving a coin)
 - up, down (removing coin from index 0)
 - enter (confirm coin move)


VALID= ||||| ||| tests
INVALID= ||||| ||  tests
BOUNDARY= ||||| | tests
4 other
25 tests total.

# Player Name Testing

### Testing: Player name input **(VALID)**
Each player is asked their name and this is stored. If they enter a blank name, it should not accept this and ask them again.

#### Test Data To Use
eg
`"Connor", "bingus"`

#### Expected Test Result
The program should store the player's name and continue


## Testing: Player name input **(INVALID)**
If the player attempts to or accidentally enters a blank name, it should prompt them to enter their name again. 
A blank name is one with no characters or comprised entirely of invisible characters.

### Test Data To Use
`"", "  ", "                     "`

### Expected Test Result
The program should say something like "Please enter a valid name" or "Do not enter a blank name!" and ask the user for their name again.


---
# Moving cursor to select a coin (left, right, a, d)


## Testing: Moving Cursor to select coin **(VALID)**
The program has to get input on each player's turn on what coin they want to move. They should do this by pressing the arrow keys or WASD to move a cursor left or right to above wished coin to select.
### Test Data To Use
Use arrow keys to navigate *directly* to a valid coin and press enter. This **will not** involve selecting an invalid coin or navigating off the board. 
e.g. to select a coin at index 2 starting at index 0.
`RIGHT, RIGHT, ENTER` 
and all other combinations like
`RIGHT, RIGHT, LEFT, RIGHT, ENTER`
also accepted as they result in cursor at the same place without moving to the edges of the board

### Expected Test Result
The program should move the cursor left if the player presses left and right if they press right.



## Testing: Moving cursor to select coin ends of board **(BOUNDARY)**
The program has to get input on each player's turn on what coin they want to move. They should do this by pressing the arrow keys or WASD to move a cursor left or right to above wished coin to select.
### Test Data To Use
We will use arrow keys to move cursor to the very edges of the board.
`RIGHT x11` (starting at 0 and moving to slot 11)
### Expected Test Result
The cursor should move to the very right end of the board on the 11th right key press.



## Testing: Moving cursor to select coin **(INVALID)**
The program has to get input on each player's turn on what coin they want to move. They should do this by pressing the arrow keys or WASD to move a cursor left or right to select the coin and then press enter to select the coin.
### Test Data To Use
We will use **keys other than arrow and WASD and enter/space**.
eg XZC(*&CJHVK
### Expected Test Result
We should be able to hammer on the keyboard and the program should reject all irrelevant key presses with no changes to game state.


## Testing: Moving cursor to select coin **(INVALID)**
Testing whether the cursor will not move off the board.

### Test Data To Use
Move cursor to far left or far right of the board and try to move past this.
eg `LEFT` or `RIGHT x12`

### Expected Test Result
The cursor should not be able to move off the board under any circumstance and so the program will ignore this input.

---
# Confirming coin select

## Testing: Selecting a valid coin
When the cursor is over a coin that can be moved by more than one space (so not boundary), the user should press space and enter and the program stores the coin and enters selection mode.

### Test Data To Use
Select a coin with more than one space to the left able to be moved 
eg selecting coin in this example.
`*__C .....`

### Expected Test Result
The program should store this coin and move into moving mode.


## Testing: Selecting coin with only one possible move (BOUNDARY)
Selecting a coin where there is only one possible place to move is a boundary case but still possible. 
This tests the program recognising how many moves are possible for a coin.
### Test Data To Use
Select 2nd ex coin
`*_C ...`
### Expected Test Result
The program should store this coin and move into moving mode.


## Testing: Selecting coin with 11 possible moves (BOUNDARY)
Test whether we can select a coin with the maximum amount of moves possible for given board size to test the
program recognising how many moves are possible for a coin.
### Test data to use
`__________G`
move cursor to far right and press `ENTER` to select coin
### Expected test result
The program should let us select the coin and then move into moving the coin mode.



## Testing: Selecting a coin in the far left of the board (BOUNDARY)
The coin in the left cell about to be removed is a boundary case but the user should be able to select this.
### Test Data To Use
Select coin in ex:
`C**** ...`
### Expected Test Result
The program should store this and enter special moving mode to allow this coin - or rather cursor at this stage - to be moved 'up and down' on and off the board.



## Testing: Selecting a coin with no moves (INVALID)
If there is a coin immediately to the left of our coin, there are no valid moves and therefore the coin should not be selectable.
### Test Data To Use
press enter/space to select index 3 in this example.
`*CC ...`
### Expected Test Result
The program should reject this input, it is invalid.

## Testing: Selecting an empty space (INVALID)
If the user tries to selects an empty cell, this is not a valid coin to select.
### Test data to use
`C_ ...`
pressing enter on index two of ex.
### Expected Test Result
The program should reject the input; nothing will occur.


---
# Moving a Coin


## Testing: Moving A Coin (INVALID)

The user should only be able to move the cursor to the left of the coin they have selected that they want to move.
This is to prevent the user moving a coin to the right initially as it is a clear indication that they can not do this.

### Test Data To Use

We can select a coin and then try to move the cursor right e.g. from index 0 to coin at 2:
`D, D, ENTER, D x12, RIGHT, RIGHT`

### Expected Test Result
The cursor should move right two spaces, the coin should be selected, then the cursor will not move right no matter how many times the user presses right.


## Testing: Moving a coin (INVALID)
The user should only be able to move their cursor to a valid place on the board. This means that when moving, the cursor should not be able to
be moved further to the left than the greatest possible move, ie not be able to be moved onto the next coin on the board, only empty spaces.

### Test Data To Use
following setup
`C___C ...`
right coin selected then: ` LEFT x3, A x2, LEFT`
### Expected Test Result
The cursor should end up on the right of the left coin on 3rd keypress and not move any further left.
`C|__C`

## Testing: Moving a coin (VALID)
The user should be able to move their cursor as many places left as they wish as long as it is a valid move.
### Test Data to Use
`___C ...`
enter/space pressed at index 3 of ex then: `LEFT x2`
### Expected Test Result
The cursor should end up at index 1, and the program should be ready to accept the press of enter to execute this move.


---
# Confirming Coin Move

## Testing: Confirm coin move (VALID)
Testing whether once a coin is selected and cursor moved to a different valid slot the program will move the coin and switch player's turns once
enter is pressed.
### Test Data to Use
`_|_C`
`ENTER`
### Expected Test Result
The coin will move to where the user has selected and it will switch turns to the other player
`_C__`

## Testing: Confirm coin not move and return to selecting (VALID)
The user should be able to cancel moving a selected coin by 'trying to move the coin to it's original position'
### Test Data to Use
Press enter to select coin
`ENTER`
Not move cursor and press enter
`ENTER`
### Expected Test Result
The coin will not move, and it will remain the players turn, ready for them to select another coin by delselecting the current coin and moving into selection mode.

## Testing: Confirming coin move (BOUNDARY)
The program should allow the user to move the coin just one slot in the board.
### Test data to use
`___|C`
`ENTER`
Select coin, move cursor to left and press enter
### Expected test result
The coin will move one space to the left and the program will progress to the next players turn.

## Testing: Confirming coin move (BOUNDARY)
The program should allow the user to move the coin the greatest possible distance (11 spaces).
### Test data to use
`|__________G`
`ENTER`
### Expected test result
The coin will move to the far left of the board and the program will progress to the next player's turn.

---
# Removing Coins

## Testing: Player removing GOLD coin (valid)
Test whether the program recognises removing a gold coin as a win
### Test Data to Use
`G___...`
`|`
(cursor in off board position with gold coin selected in index 0) press `ENTER`
### Expected Test Result
The coin will be removed and the program will immediately proceed to congratulating winning player.


## Testing: Player removing normal coin (valid)
Tests removing a normal (silver) coin from index 0 
### Test Data to Use
`C____ ...`
`|`
### Expected Test Result
The coin is removed and the program switches to the other players turn.

*there is no invalid or boundary testing for removing coins as this is handled in earlier parts of code.*

---
# Winning a game

## Testing: Player 1 winning game
Player 1 should be able to win the game by removing gold coin from board.
### Test Data to Use
Gold coin in index 0.
Player 1 uses their turn to remove coin.
### Expected Test Result
The game loop should exit immediately with the program congratulating player one by their name and exiting.

## Testing: Player 2 winning game
Player 2 should be able to win the game by removing gold coin from board.
### Test Data to Use
Gold coin in index 0.
Player 2 uses their turn to remove coin.
### Expected Test Result
The game loop should exit immediately with the program congratulating player two by their name and exiting.

---
# Miscellaneous testing


## Testing: Quitting program
The user should be able to quit out of the main game loop to exit the game.

### Test Data to Use
In game loop
`q`
### Expected Test Result
The program will exit without throwing errors or presenting with a winner.


## Testing: Board initialization
The board should generate number of gold coins according to one gold coin, NUM_SILVER_COINS and NUM_BOARD_SPACES compile time constants and generate a board size that is the sum of
all of these. We should not generate the board with a gold coin in board index 0 as the player who starts would instantly win. The board should be generated randomly.

### Test Data To Use
Run the game multiple times and observe the result of the generated board.

### Expected Test Result
Create and display a randomly generated board that has 1 gold coin, NUM_SILVER_COINS other coins and
NUM_BOARD_SPACES spaces in the board. There should only be one coin per cell and the gold coin should now spawn in the 0th index.














