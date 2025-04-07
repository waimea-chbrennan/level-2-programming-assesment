/**
 * =====================================================================
 * Programming Project for NCEA Level 2, Standard 91896
 * ---------------------------------------------------------------------
 * Project Name:   Old Gold Assessment Task
 * Project Author: Connor Brennan
 * GitHub Repo:    https://github.com/waimea-chbrennan/level-2-programming-assesment
 * =====================================================================
 */

import com.varabyte.kotter.foundation.anim.*
import com.varabyte.kotter.foundation.input.Keys
import com.varabyte.kotter.foundation.input.onKeyPressed
import com.varabyte.kotter.foundation.text.*
import com.varabyte.kotter.runtime.RunScope
import com.varabyte.kotter.runtime.Session
import com.varabyte.kotter.runtime.render.RenderScope
import com.varabyte.kotterx.decorations.BorderCharacters
import com.varabyte.kotterx.decorations.bordered
import com.varabyte.kotterx.grid.Cols
import com.varabyte.kotterx.grid.GridCharacters
import com.varabyte.kotterx.grid.grid
import com.varabyte.kotterx.text.Justification

const val playerNumToHsvMultiplier = 120


/**
 * Decides what action to take based on key pressed, refer to GameState
 */
fun RunScope.handleKeys(state: GameState ) {
    onKeyPressed {
        when(key){
            Keys.Q -> signal()
            //Only allow moving right if it is a valid place on our board and, if we are selecting, not any further right than the coin we selected
            Keys.RIGHT, Keys.D -> state.moveCursor(1)
            //Only allow moving left if it is a valid place on the board
            Keys.LEFT, Keys.A -> state.moveCursor(-1)
            Keys.DOWN, Keys.S -> state.moveCursorToFromOffBoard()
            Keys.UP, Keys.W -> state.moveCursorToFromOffBoard()
            Keys.ENTER, Keys.SPACE -> state.setCoinPosition()
        }
    }
}

/**
 * Prints the current state with instructions to the player
 */
fun RenderScope.printPlayerInfo(state: GameState){
    //hsv to show a unique colour based on player number
    hsv(playerNumToHsvMultiplier*state.playerNumber,1.0f,1.0f){ bold{ textLine("Player ${state.playerNumber} to move\n") } }
    black(isBright = true) {
        when (state.playerTurnProgress) {
            0 -> text("Select a coin to move using WASD or Arrow keys")
            1 -> text("Select where you want this coin to be moved")
        }
    }
}




/**
 * Prints the current board of coins with desired board colour and animations for coins
 */
fun RenderScope.printBoard(state: GameState, coinAnim: TextAnim) {

    /**
     * Prints an indivual cell made from a coin of certain colour and sometimes a background
     */
    fun RenderScope.printBoardCell( borderColour: Color?, coinColour: Color) {
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
                                printBoardCell( Color.GREEN, Color.BRIGHT_YELLOW)
                            } else {
                                printBoardCell( Color.WHITE, Color.BRIGHT_YELLOW)
                            }
                        } else {
                            printBoardCell(null, Color.BRIGHT_YELLOW)
                        }
                    }
                    COIN -> {
                        if(state.cursorIndex==index){
                            //
                            if(state.amountOfMovesPossible(state.cursorIndex)!=0) {
                                printBoardCell( Color.GREEN, Color.WHITE)
                            } else {
                                printBoardCell( Color.WHITE, Color.WHITE)
                            }
                        } else {
                            printBoardCell(null, Color.WHITE)

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
 * Congratulate the winning player
 */
fun Session.winScreen(state: GameState) {
    val banner = "=".repeat(25)
    section {
        bold()
        yellow(isBright = true)
        textLine(banner)

        hsv(playerNumToHsvMultiplier*state.playerNumber,1.0f,1.0f) {
            textLine("CONGRATULATIONS, PLAYER ${state.winner}")
        }

        textLine("Thanks for playing Old Gold!")
        textLine(banner)

    }.run()
}



/**
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