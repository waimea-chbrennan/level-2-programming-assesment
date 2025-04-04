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
    var index by session.liveVarOf(0)
    var selectedIndex = 0

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
    initBoard(gameState)

    section {
        printPlayerInfo(gameState)
        printBoard(gameState,coinAnim)

    }.runUntilSignal {
        handleKeys(gameState)

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
            Keys.RIGHT,Keys.D -> {
                //Only allow moving right if it is a valid place on our board and, if we are selecting, not any further right than the coin we selected
                if(state.index<state.board.lastIndex && (state.index<state.selectedIndex || state.playerTurnProgress==0) ) state.index++
            }
            Keys.LEFT,Keys.A -> {
                if(state.index>0) state.index--
            }
            Keys.DOWN,Keys.S -> {
                //We only want to remove a coin if in moving mode and cursor is in cell 0
                if(state.selectedIndex==0||state.playerTurnProgress==1) {}
            }
            Keys.ENTER,Keys.SPACE-> {
                //Check whether valid coin selected
                if(state.board[state.index]!=EMPTY){
                    //Move into moving mode if we are not already
                    if(state.playerTurnProgress==0) state.playerTurnProgress=1
                    state.selectedIndex = state.index

                }
            }

        }
    }
}


/**
 * printPlayerInfo()
 * This function will print the current state with instructions to the player
 */
fun RenderScope.printPlayerInfo(state: GameState){
    cyan{ bold{ textLine("Player ${state.playerNumber} to move\n") } }
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
                        if(state.index==index){
                            green()
                            grid(Cols(9), characters = GridCharacters.CURVED, justification = Justification.CENTER){
                                cell {
                                    yellow(isBright = true)
                                    bold()
                                    textLine(coinAnim)
                                }
                            }
                        } else {
                            textLine()
                            yellow(isBright = true)
                            bold()
                            textLine(coinAnim)
                        }
                    }
                    COIN -> {
                        if(state.index==index){
                            green()
                            grid(Cols(9), characters = GridCharacters.CURVED, justification = Justification.CENTER){
                                cell {
                                    white()
                                    text(coinAnim)
                                }
                            }
                        } else {
                            textLine()
                            textLine(coinAnim)
                            textLine(" ")

                        }
                    }
                    EMPTY -> {
                        if (state.index == index) {
                            white {
                                bordered(BorderCharacters.CURVED) {
                                }
                            }
                        }

                    }
                }
            }
            }
        }

    }



/**
 * initBoard()
 * Does the initial setup to place and randomize coins.
 */
fun initBoard(state: GameState) {
    //We want to add one gold coin and a random amount of other coins
    state.board.add(GOLD_COIN)
    for(i in 1..NUM_SILVER_COINS) state.board.add(COIN)
    for(i in 1..NUM_BOARD_SPACES) state.board.add(EMPTY)
    //Ensure gold coin does not start at index 0, game would be no fun as player 1 would win instantly!
    while(state.board[0]==GOLD_COIN) state.board.shuffle(Random(System.currentTimeMillis())) //Have to specify source of randomness bug ?
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