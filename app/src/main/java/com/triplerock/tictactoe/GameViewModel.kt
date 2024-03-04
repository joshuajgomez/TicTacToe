package com.triplerock.tictactoe

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel

data class Crossing(val start: Offset, val end: Offset, val positions: List<Int>)

const val rectOffset = 650f
val rect = Rect(Offset.Zero, Size(width = rectOffset, height = rectOffset))
val crossingList = listOf(
    Crossing(positions = listOf(0, 1, 2), start = rect.topLeft, end = rect.topRight),
    Crossing(positions = listOf(3, 4, 5), start = rect.centerLeft, end = rect.centerRight),
    Crossing(positions = listOf(6, 7, 8), start = rect.bottomLeft, end = rect.bottomRight),

    Crossing(positions = listOf(0, 3, 6), start = rect.topLeft, end = rect.bottomLeft),
    Crossing(positions = listOf(1, 4, 7), start = rect.topCenter, end = rect.bottomCenter),
    Crossing(positions = listOf(2, 5, 8), start = rect.topRight, end = rect.bottomRight),

    Crossing(positions = listOf(0, 4, 8), start = rect.topLeft, end = rect.bottomRight),
    Crossing(positions = listOf(2, 4, 6), start = rect.topRight, end = rect.bottomLeft),
)

data class Winner(
    val player: Int,
    val crossing: Crossing,
    val message: String = "Player$player won!! Congrats",
)

class GameViewModel : ViewModel() {

    private val player1Moves: MutableList<Int> = mutableListOf()
    private val player2Moves: MutableList<Int> = mutableListOf()

    var playerWon: MutableIntState = mutableIntStateOf(0)
    lateinit var winner: Winner

    fun onPlayer1Moved(cell: Int) {
        player1Moves.add(cell)
        checkStatus(1, player1Moves.toList())
    }

    fun onPlayer2Moved(cell: Int) {
        player2Moves.add(cell)
        checkStatus(2, player2Moves.toList())
    }

    private fun checkStatus(player: Int, moves: List<Int>) {
        for (crossing in crossingList) {
            if (moves.containsAll(crossing.positions)) {
                winner = Winner(player = player, crossing = crossing)
                playerWon.value = player
            }
        }
    }
}