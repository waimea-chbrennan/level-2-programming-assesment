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

import com.varabyte.kotterx.grid.*
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

const val GOLD_COIN = 'G'
const val COIN = 'c'
const val EMPTY = '-'




fun main() = session {
    val goldCoinAnim = textAnimOf(listOf("G","G","O"), 125.milliseconds)
    val board = liveListOf<Char>()
    initBoard(board)

    section {
        printBoard(board,goldCoinAnim)

    }.runUntilSignal {
        onKeyPressed {
            when(key){
                Keys.Q -> signal()
            }
        }

    }
}

fun RenderScope.printBoard(board: List<Char>,goldCoinAnim: TextAnim) {
    grid(
        Cols.uniform(board.size,3),
        characters = GridCharacters.CURVED,
        maxCellHeight = 1,
    ){
        board.forEachIndexed { index, slot ->
            cell(col=index) {
                when(slot){
                    GOLD_COIN -> text(goldCoinAnim)
                    COIN -> text(COIN)
                    EMPTY -> text(EMPTY)
                }
            }
        }

    }

}

fun initBoard(board: MutableList<Char>) {
    //We want to add one gold coin and a random amount of other coins
    board.add(GOLD_COIN)
    for(i in 1..5) board.add(COIN)
    for(i in 1..6) board.add(EMPTY)
    board.shuffle(Random(System.currentTimeMillis())) //Have to specify source of randomness bug ?
}