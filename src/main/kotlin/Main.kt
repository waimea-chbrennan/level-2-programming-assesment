/**
 * =====================================================================
 * Programming Project for NCEA Level 2, Standard 91896
 * ---------------------------------------------------------------------
 * Project Name:   Old Gold Asesment Task
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




fun main() = session {
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
    val board = liveListOf<Char>()
    var player by liveVarOf(1)
    var playerTurnProgress by liveVarOf(0) // 0=selecting 1=moving
    var index by liveVarOf(0)

    welcomeIntro()

    initBoard(board)

    section {
        printPlayerInfo(player, playerTurnProgress)

        printBoard(board,coinAnim,index)
        printSelectionCursor(board, index, playerTurnProgress)

    }.runUntilSignal {
        onKeyPressed {
            when(key){
                Keys.Q -> signal()
                Keys.RIGHT -> index++
                Keys.LEFT -> index--

            }
        }

    }


}


fun RenderScope.printSelectionCursor(board: List<Char>, index: Int, playerTurnProgress: Int){

}



fun RenderScope.printPlayerInfo(player: Int, playerTurnProgress: Int){
    cyan{ bold{ textLine("Player $player to move\n") } }
    black(isBright = true) {
        when (playerTurnProgress) {
            0 -> text("Select a coin to move using WASD or Arrow keys")
            1 -> text("Select where you want this coin to be moved")
        }
    }
}




/**
 * printBoard()
 * Prints the current board of coins with desired board colour and animations for coins
 */
fun RenderScope.printBoard(board: List<Char>,coinAnim: TextAnim,selectedIndex: Int) {
    grid(
        Cols.uniform(board.size,11),
        characters = GridCharacters.CURVED,
        maxCellHeight = 5,
        justification = Justification.CENTER,
    ){
        board.forEachIndexed { index, slot ->
            cell(col=index) {
                printBoardCell(coinAnim, index, selectedIndex, slot)
            }
        }

    }

}
fun RenderScope.printBoardCell(coinAnim: TextAnim, index: Int, selectedIndex: Int, coinType: Char){
    when(coinType) {
        GOLD_COIN -> {
            if(selectedIndex==index){
                green()
                grid(Cols(9), characters = GridCharacters.CURVED, justification = Justification.CENTER,){
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
            if(selectedIndex==index){
                green()
                grid(Cols(9), characters = GridCharacters.CURVED, justification = Justification.CENTER,){
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
            if (selectedIndex == index) {
                white {
                    bordered(BorderCharacters.CURVED) {
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
fun initBoard(board: MutableList<Char>) {
    //We want to add one gold coin and a random amount of other coins
    board.add(GOLD_COIN)
    for(i in 1..5) board.add(COIN)
    for(i in 1..6) board.add(EMPTY)
    //Ensure gold coin does not start at index 0, game would be no fun
    while(board[0]==GOLD_COIN) board.shuffle(Random(System.currentTimeMillis())) //Have to specify source of randomness bug ?
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