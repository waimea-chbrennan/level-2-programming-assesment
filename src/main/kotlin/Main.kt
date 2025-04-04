/**
 * =====================================================================
 * Programming Project for NCEA Level 2, Standard 91896
 * ---------------------------------------------------------------------
 * Project Name:   Old Gold Assessment Task
 * Project Author: Connor Brennan
 * GitHub Repo:    https://github.com/waimea-chbrennan/level-2-programming-assesment
 * ---------------------------------------------------------------------
 * Notes:
 * This is a two-player game, played on a one-dimensional grid with coins, where the aim is to win by being the player who removes the gold coin.
 * =====================================================================
 */
import com.varabyte.kotter.foundation.*
import com.varabyte.kotter.foundation.collections.liveListOf
import com.varabyte.kotter.foundation.input.*
import com.varabyte.kotter.foundation.text.*
import com.varabyte.kotter.runtime.render.*
import com.varabyte.kotter.foundation.anim.*
import com.varabyte.kotter.runtime.RunScope
import com.varabyte.kotter.runtime.Session
import com.varabyte.kotterx.decorations.BorderCharacters
import com.varabyte.kotterx.decorations.bordered

import com.varabyte.kotterx.grid.*
import com.varabyte.kotterx.text.Justification
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

const val GOLD_COIN = 'G'
const val COIN = 'c'
const val EMPTY = '-'
const val NUM_SILVER_COINS = 5
const val NUM_BOARD_SPACES = 6

class GameState(session: Session) {
    val board = session.liveListOf<Char>()
    var playerNumber by session.liveVarOf(1)
    var playerTurnProgress by session.liveVarOf(0) // 0 = selecting, 1 = moving
    var cursorIndex by session.liveVarOf(0)
    var selectedIndex = 0
    var winner = -1
    var onWin: (() -> Unit)? = null

    fun moveCursorToFromOffBoard() {
        //only move cursor when moving coin from index 0
        if(selectedIndex!=0||playerTurnProgress!=1) return
        if(cursorIndex == 0) cursorIndex = -1 else cursorIndex = 0
    }


    fun moveCursor(offset: Int) {
        //Ensure cursor will be moved within the board
        if(cursorIndex+offset>board.lastIndex) return
        if(cursorIndex+offset<0) return

        //If we are moving a coin, the cursor should not be right of the selectedIndex
        if(cursorIndex+offset>selectedIndex && playerTurnProgress==1) return
        //If we are moving a coin, we should not be able to jump over a coin
        if(cursorIndex+offset<selectedIndex-amountOfMovesPossible(selectedIndex) && playerTurnProgress==1) return

        cursorIndex+=offset
    }



    fun setCoinPosition() {

        fun setSelectedCoin() {
            //Useless selecting no coin at all or a coin that can't be moved
            if(board[cursorIndex]==EMPTY) return
            if(amountOfMovesPossible(cursorIndex)==0) return
            selectedIndex = cursorIndex
            //we are now ready to be in moving mode
            playerTurnProgress = 1
        }

        fun moveSelectedCoin() {
            //we know that we have selected a valid coin that can be moved and that our cursor is in a valid place to move to
            //only move the coin and empty old cell if position warrants it
            if(cursorIndex!=selectedIndex) {
                //handle edge case of removing coin from index 0
                if(selectedIndex==0&&cursorIndex==-1){
                    //check winning move
                    if(board[selectedIndex]==GOLD_COIN) {
                        winner=playerNumber
                        onWin?.invoke()
                        board[selectedIndex] = EMPTY
                        return
                    }
                    board[selectedIndex] = EMPTY
                } else {
                    board[cursorIndex] = board[selectedIndex]
                    board[selectedIndex] = EMPTY
                }


                //player turn now finished, switch to other player, set cursor to start and switch to selecting mode
                if(playerNumber==1) playerNumber = 2 else playerNumber = 1
                cursorIndex = 0
            }
            playerTurnProgress = 0
        }

        if(playerTurnProgress==0) {
            setSelectedCoin()
        } else {
            moveSelectedCoin()
        }
    }


    /**
     * amountOfMovesPosible()
     * Returns the amount of moves that could be made with the current board state
     * if a player were to select the specified index
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
     * initBoard()
     * Does the initial setup to place and randomize coins.
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


fun main() = session {
    val gameState = GameState(this)

    val coinAnim = textAnimOf(listOf("""
        /===\
        | $ |
        \===/
    """.trimIndent(),"""
        /==\
        | s|
        \==/
    """.trimIndent(),"""
         []
         []
         []
    """.trimIndent(),"""
        /==\
        | .|
        \==/
    """.trimIndent(),"""
        /===\
        | $ |
        \===/
    """.trimIndent()), 150.milliseconds)



    welcomeIntro()
    gameState.initBoard()

    section {
        printPlayerInfo(gameState)
        printBoard(gameState,coinAnim)
    }.runUntilSignal {
        handleKeys(gameState)
        gameState.onWin = { signal() }
    }

}

/**
 * handleKeys()
 * Decides what action to take when keys pressed based off of the moveState
 */
fun RunScope.handleKeys(state: GameState ) {

    onKeyPressed {
        when(key){
            Keys.Q -> signal()
            //Only allow moving right if it is a valid place on our board and, if we are selecting, not any further right than the coin we selected
            Keys.RIGHT,Keys.D -> state.moveCursor(1)
            //Only allow moving left if it is a valid place on the board
            Keys.LEFT,Keys.A -> state.moveCursor(-1)
            Keys.DOWN,Keys.S -> state.moveCursorToFromOffBoard()
            Keys.UP,Keys.W -> state.moveCursorToFromOffBoard()
            Keys.ENTER,Keys.SPACE -> state.setCoinPosition()
        }
    }
}

/**
 * printPlayerInfo()
 * This function will print the current state with instructions to the player
 */
fun RenderScope.printPlayerInfo(state: GameState){
    //hsv to show a unique colour based on player number
    hsv(80*state.playerNumber,1.0f,1.0f){ bold{ textLine("Player ${state.playerNumber} to move\n") } }
    black(isBright = true) {
        when (state.playerTurnProgress) {
            0 -> text("Select a coin to move using WASD or Arrow keys")
            1 -> text("Select where you want this coin to be moved")
        }
    }
}




/**
 * printBoard()
 * Prints the current board of coins with desired board colour and animations for coins
 */
fun RenderScope.printBoard(state: GameState, coinAnim: TextAnim) {

    fun RenderScope.printBoardCell(state: GameState, borderColour: Color?, coinColour: Color) {
        //print coin with no border
        if(borderColour == null){
            textLine()
            color(coinColour)
            textLine(coinAnim)
            textLine(" ")
        } else {
            //Print coin with border
            color(borderColour)
            grid(Cols(9), characters = GridCharacters.CURVED, justification = Justification.CENTER){
                cell {
                    color(coinColour)
                    text(coinAnim)
                }
            }
        }
    }

    grid(
        Cols.uniform(state.board.size,11),
        characters = GridCharacters.CURVED,
        maxCellHeight = 5,
        justification = Justification.CENTER,
    ){
        state.board.forEachIndexed { index, slot ->
            cell(col=index) {
                when(slot) {
                    GOLD_COIN -> {
                        if(state.cursorIndex==index){
                            if(state.amountOfMovesPossible(state.cursorIndex)!=0) {
                                printBoardCell(state, Color.GREEN, Color.BRIGHT_YELLOW)
                            } else {
                                printBoardCell(state, Color.WHITE, Color.BRIGHT_YELLOW)
                            }
                        } else {
                            printBoardCell(state,null,Color.BRIGHT_YELLOW)
                        }
                    }
                    COIN -> {
                        if(state.cursorIndex==index){
                            //
                            if(state.amountOfMovesPossible(state.cursorIndex)!=0) {
                                printBoardCell(state, Color.GREEN, Color.WHITE)
                            } else {
                                printBoardCell(state, Color.WHITE, Color.WHITE)
                            }
                        } else {
                            printBoardCell(state,null,Color.WHITE)

                        }
                    }
                    EMPTY -> {
                        if (state.cursorIndex == index) {
                            if(state.playerTurnProgress==1){
                                green()
                            } else {
                                white()
                            }
                            bordered(BorderCharacters.CURVED) {}
                        }
                    }

                }
            }
        }
    }
    grid(
        Cols.uniform(1,11),
        characters = GridCharacters.CURVED,
        maxCellHeight = 5,
        justification = Justification.CENTER
    ){
        cell{
            if(state.cursorIndex==-1){
                textLine(" ")
                green()
                bordered(BorderCharacters.CURVED) {}
                textLine(" ")
            } else {
                black(isBright = true)
                textLine("\n↓ to remove coin from slot 1 \n ")
            }

        }
    }


}


/**
 * welcomeIntro()
 * Welcome the player
 */
fun Session.welcomeIntro() {
    section {
        bold()
        yellow(isBright = true)
        textLine("Welcome to the Old Gold Game!")
        textLine()

    }.run()
}