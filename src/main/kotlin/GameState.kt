/**
 * =====================================================================
 * Programming Project for NCEA Level 2, Standard 91896
 * ---------------------------------------------------------------------
 * Project Name:   Old Gold Assessment Task
 * Project Author: Connor Brennan
 * GitHub Repo:    https://github.com/waimea-chbrennan/level-2-programming-assesment
 *
 * This file contains GameState, handling core game logic such as turns and other player info
 * =====================================================================
 */

import com.varabyte.kotter.foundation.collections.liveListOf
import com.varabyte.kotter.foundation.liveVarOf
import com.varabyte.kotter.runtime.Session
import kotlin.random.Random

const val GOLD_COIN = 'G'
const val COIN = 'c'
const val EMPTY = '-'
const val NUM_SILVER_COINS = 5
const val NUM_BOARD_SPACES = 6

//States for playerTurnProgress
const val PLAYER_SELECTING = 0
const val PLAYER_MOVING = 1


/**
 * Handles core game logic, such as turns and other player info
 * @param session Allows for use of kotter Session scoped liveListOf etc
 */
class GameState(session: Session) {
    val board = session.liveListOf<Char>()
    var playerNumber by session.liveVarOf(1)
    var playerNames = mutableListOf<String>()
    var playerTurnProgress by session.liveVarOf(PLAYER_SELECTING) // 0 = selecting, 1 = moving
    var cursorIndex by session.liveVarOf(0)
    private var selectedIndex = 0
    var winner = -1 //Start invalid with no winner
    var onWin: (() -> Unit)? = null


    


    /**
     * Handles the half-slot of the off-board position. we do not need this position to be in board as it does not
     * need to hold a coin, only a cursor. We consider the cursorIndex at -1 to be here
     */
    fun moveCursorToFromOffBoard() {
        //only move cursor when moving coin from index 0
        if(selectedIndex!=0||playerTurnProgress!=PLAYER_MOVING) return
        cursorIndex = if(cursorIndex == 0) -1 else 0
    }

    /**
     * Updates cursor position safely
     * @param offset The number of positions to move the cursor. Positive = right , negative = left
     */
    fun moveCursor(offset: Int) {
        val wishedIndex = cursorIndex+offset
        //Ensure cursor will be moved within the board
        if(wishedIndex>board.lastIndex) return
        if(wishedIndex<0) return

        //If we are moving a coin, the cursor should not be right of the selectedIndex
        if(wishedIndex>selectedIndex && playerTurnProgress==PLAYER_MOVING) return
        //If we are moving a coin, we should not be able to jump over a coin
        if(wishedIndex<selectedIndex-amountOfMovesPossible(selectedIndex) && playerTurnProgress==PLAYER_MOVING) return

        cursorIndex+=offset
    }

    /**
     * Handle whether to select or move a coin.
     */
    fun handleSelectOrMove() {
        if(playerTurnProgress==PLAYER_SELECTING) {
            setSelectedCoin()
        } else {
            moveSelectedCoin()
        }
    }

    /**
     * Selects only a valid coin and moves into selection mode
     * @see handleSelectOrMove
     */
    private fun setSelectedCoin() {
        //Useless selecting no coin at all or a coin that can't be moved
        if(board[cursorIndex]==EMPTY) return
        if(amountOfMovesPossible(cursorIndex)==0) return
        selectedIndex = cursorIndex
        //we are now ready to be in moving mode
        playerTurnProgress = PLAYER_MOVING
    }

    /**
     * Moves the selected coin to the selected position on (or off) board
     * @see handleSelectOrMove
     */
    private fun moveSelectedCoin() {
        //cancel the move if the player wants to move coin to original position
        if(cursorIndex==selectedIndex) {
            playerTurnProgress=PLAYER_SELECTING
            return
        }
        //handle edge case of removing coin from index 0
        if(selectedIndex==0){
            //if we are removing gold coin, move has won
            if(board[selectedIndex]==GOLD_COIN) {
                winner=playerNumber
                onWin?.invoke()
                board[selectedIndex] = EMPTY
                return
            }
        } else { //Normal board case
            board[cursorIndex] = board[selectedIndex]
        }
        //Still could be removing a normal coin from 0 or a regular move.
        board[selectedIndex] = EMPTY

        //player turn now finished, switch to other player, set cursor to start and switch to selecting mode
        playerNumber = if(playerNumber==1) 2 else 1
        cursorIndex = 0
        playerTurnProgress = PLAYER_SELECTING
    }


    /**
     * Returns the amount of moves that could be made with the current board state
     * @param atIndex The index of the board to inspect moves with
     * @return The amount of moves possible, 0 if none
     */
    fun amountOfMovesPossible(atIndex:Int): Int{
        //cant move an empty space
        if(board[atIndex]==EMPTY) return 0
        //Taking coins off the board in index 0 is a special case
        if(atIndex==0) return 1

        for(i in 1..atIndex+1){
            //check atIndex-i==-1 to return if out of bounds (if no coins from atIndex to end of board
            if(atIndex-i==-1 || board[atIndex-i]!=EMPTY ) return i-1
        }
        return atIndex

    }


    /**
     * Initial setup to place and randomize coins.
     */
    fun initBoard() {
        //We want to add one gold coin and a random amount of other coins
        board.add(GOLD_COIN)
        for(i in 1..NUM_SILVER_COINS) board.add(COIN)
        for(i in 1..NUM_BOARD_SPACES) board.add(EMPTY)
        //Ensure gold coin does not start at index 0, game would be no fun as player 1 would win instantly!
        while(board[0]==GOLD_COIN) board.shuffle(Random(System.currentTimeMillis())) //Have to specify source of randomness bug ?
    }
}