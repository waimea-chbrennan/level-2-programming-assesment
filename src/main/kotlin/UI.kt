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
import com.varabyte.kotter.foundation.input.*
import com.varabyte.kotter.foundation.liveVarOf
import com.varabyte.kotter.foundation.runUntilSignal
import com.varabyte.kotter.foundation.text.*
import com.varabyte.kotter.foundation.timer.addTimer
import com.varabyte.kotter.runtime.RunScope
import com.varabyte.kotter.runtime.Session
import com.varabyte.kotter.runtime.render.RenderScope
import com.varabyte.kotterx.decorations.BorderCharacters
import com.varabyte.kotterx.decorations.bordered
import com.varabyte.kotterx.grid.Cols
import com.varabyte.kotterx.grid.GridCharacters
import com.varabyte.kotterx.grid.grid
import com.varabyte.kotterx.text.Justification
import kotlin.time.Duration.Companion.milliseconds

//Use this to define the separation between player colours. Max range 1-180 inclusive
const val PLAYER_HSV_MULT = 180

val GOLD_COL = Color.BRIGHT_YELLOW
val SILVER_COL = Color.WHITE
val SELECTABLE_COL = Color.GREEN
val UNSELECTABLE_COL = Color.WHITE


/**
 * Decides what action to take based on key pressed, refer to GameState
 * @param state Calls GameState functions to handle key presses
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
            Keys.ENTER, Keys.SPACE -> state.handleSelectOrMove()
        }
    }
}

/**
 * Prints the current state with instructions to the player
 * @param state Used for getting playerNumber information
 */
fun RenderScope.printPlayerInfo(state: GameState){
    //hsv to show a unique colour based on player number
    hsv(PLAYER_HSV_MULT*state.playerNumber,1.0f,1.0f){ bold{ textLine("Player ${state.playerNumber}: ${state.playerNames[state.playerNumber-1]} to move\n") } }
    black(isBright = true) {
        when (state.playerTurnProgress) {
            PLAYER_SELECTING -> text("Select a coin to move using WASD or Arrow keys")
            PLAYER_MOVING -> text("Select where you want this coin to be moved")
        }
    }
}

/**
 * Prints the current board of coins with desired board colour and animations for coins
 * @param state A copy of the current GameState, used for board data operations.
 * @param coinAnim The textAnimOf the coin animation used for all coins in game
 */
fun RenderScope.printBoard(state: GameState, coinAnim: TextAnim) {
    //We iterate over the board and print each of three options. Nothing, regular coin, or gold coin
    //Each of these has three options. SELECTABLE_COL, UNSELECTABLE_COL or no border colour.
    textLine()
    grid(
        Cols.uniform(state.board.size,11),
        characters = GridCharacters.CURVED,
        maxCellHeight = 5,
        justification = Justification.CENTER,
    ){
        state.board.forEachIndexed { index, slot ->
            cell(col=index) {
                when(slot) {
                    GOLD_COIN ->handleBoardCell(state,index,GOLD_COL,coinAnim)
                    COIN -> handleBoardCell(state,index,SILVER_COL,coinAnim)
                    EMPTY -> {
                        //Can't extract this into printBoardCell as receivership of TextAnim and String as one param is not supported and other logic would be less efficient
                        if (state.cursorIndex == index) {
                            if(state.playerTurnProgress==1){
                                color(SELECTABLE_COL)
                            } else {
                                color(UNSELECTABLE_COL)
                            }
                            bordered(BorderCharacters.CURVED) {}
                        }
                    }

                }
            }
        }
    }
    //Print the off-board grid cell below the board at index 0
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
 * Handles logic as to select border based on GameState and prints text of colour supplied
 * @param state Used for checking cursorIndex
 * @param index The current index of looping over the game board
 * @param coinColor Desired colour to print coin in e.g. SILVER_COL or GOLD_COL
 * @param coinAnim The animation of the spinning coin
 */
fun RenderScope.handleBoardCell(state: GameState, index: Int, coinColor: Color, coinAnim: TextAnim) {
    //We need a cursor
    if(state.cursorIndex==index){
        if(state.amountOfMovesPossible(state.cursorIndex)!=0) {
            printBoardCell(SELECTABLE_COL, coinColor, coinAnim)
        } else {
            printBoardCell(UNSELECTABLE_COL, coinColor, coinAnim)
        }
    } else { //We dont need a cursor
        printBoardCell(null, coinColor, coinAnim)
    }
}

/**
 * Prints an individual cell made from a coin of certain colour and sometimes a background
 * @param borderColour Controls the colour of the border, null for transparent
 * @param coinColour The colour of the coin to print
 * @param coinAnim The animation of the spinning coin
 */
fun RenderScope.printBoardCell(borderColour: Color?, coinColour: Color, coinAnim: TextAnim) {
    //print coin with no border
    if(borderColour == null){
        textLine()
        color(coinColour)
        textLine(coinAnim)
        textLine(" ")
    } else {
        //Print coin with border
        color(borderColour)
        //We have to use a grid for constant size because the bordered method will change size with animation frames.
        grid(Cols(9), characters = GridCharacters.CURVED, justification = Justification.CENTER){
            cell {
                color(coinColour)
                text(coinAnim)
            }
        }
    }
}

/**
 * Congratulate the winning player
 * @param state Used for getting the winning player number of the game
 */
fun Session.winScreen(state: GameState) {
    val banner = "=".repeat(25)
    section {
        bold()
        yellow(isBright = true)
        textLine(banner)

        text("CONGRATULATIONS, ")
        hsv(PLAYER_HSV_MULT*state.winner,1.0f,1.0f) {
            textLine("PLAYER ${state.winner}")
        }

        textLine("Thanks for playing Old Gold!")
        textLine(banner)

    }.run()
}

/**
 * Welcome the player and get player names
 * @param state Used for updating listOf player names
 */
fun Session.welcomeIntro(state: GameState) {
    section {
        black(isBright = true)
        bold {
        yellow(isBright = true) {
        textLine("Welcome to the Old Gold Game!") }}
        textLine()
        text("2 players will take turns selecting and then moving a coin left on the board, with the aim of removing the ")
        bold{yellow(isBright = true){textLine("GOLD COIN!")} }
        textLine("Coins in the first slot on the board can be removed by pressing ↑/↓")
        textLine()
    }.run()

    var error by liveVarOf(false)
    for(i in 1..2) {
        section {
            if (error) red { textLine("Please do not enter a blank name!") }
            hsv(PLAYER_HSV_MULT * i, 1.0f, 1.0f) { text("PLAYER $i ") }; text("Please enter your name: "); input()
        }.runUntilSignal {
            onInputEntered {
                if (input.isNotBlank()) {
                    state.playerNames.add(input)
                    signal()
                } else {
                    error = true
                    addTimer(1000.milliseconds) { error = false }
                }
            }
        }
    }
    //Add some space under player names
    section {
        textLine()
        textLine()
    }.run()


}