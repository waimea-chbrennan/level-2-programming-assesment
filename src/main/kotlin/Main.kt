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
import com.varabyte.kotter.foundation.anim.textAnimOf
import com.varabyte.kotter.foundation.runUntilSignal
import com.varabyte.kotter.foundation.session
import kotlin.time.Duration.Companion.milliseconds

//Time between each frame of the coin animation
const val COIN_ANIM_FRAME_TIME_MS = 200

fun main() = session {
    val gameState = GameState(this)
    //coinAnim unfortunately HAS to be here due to kotter not scoping textAnimOf anywhere else
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
    """.trimIndent()), COIN_ANIM_FRAME_TIME_MS.milliseconds)

    welcomeIntro()
    gameState.initBoard()

    section {
        printPlayerInfo(gameState)
        printBoard(gameState,coinAnim)
    }.runUntilSignal {
        handleKeys(gameState)
        //callback function allows to exit instantly on win rather than waiting for key press to update.
        gameState.onWin = { signal() }
    }

    //Don't show a winning screen if user has exited through Q or CTRL+C etc.
    if(gameState.winner!=-1) winScreen(gameState)

}