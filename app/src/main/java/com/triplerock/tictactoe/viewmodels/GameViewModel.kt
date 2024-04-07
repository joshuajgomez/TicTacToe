package com.triplerock.tictactoe.viewmodels

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Crossing(val start: Offset, val end: Offset, val positions: List<Int>)

const val rectOffset = 600f
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

sealed class GameUiState {
    data class Ready(
        val statusText: String = "Player 1 starts",
    ) : GameUiState()

    data class NextTurn(
        val player1Moves: List<Int>,
        val player2Moves: List<Int>,
        val statusText: String,
    ) : GameUiState()

    data class Draw(
        val player1Moves: List<Int>,
        val player2Moves: List<Int>,
        val statusText: String = "Its a draw!",
    ) : GameUiState()

    data class Winner(
        val player1Moves: List<Int>,
        val player2Moves: List<Int>,
        val statusText: String,
        val crossing: Crossing,
    ) : GameUiState()
}

class GameViewModel : ViewModel() {

    private var isTurnOfPlayer1 = true

    private val player1Moves: ArrayList<Int> = ArrayList()
    private val player2Moves: ArrayList<Int> = ArrayList()

    private val _uiState: MutableStateFlow<GameUiState> = MutableStateFlow(GameUiState.Ready())
    val uiState: StateFlow<GameUiState> = _uiState

    fun onCellClick(cell: Int) {
        if (isTurnOfPlayer1) {
            player1Moves.add(cell)
            checkState(1, player1Moves)
        } else {
            player2Moves.add(cell)
            checkState(2, player2Moves)
        }
        isTurnOfPlayer1 = !isTurnOfPlayer1
    }

    private fun checkState(player: Int, moves: List<Int>) {
        for (crossing in crossingList) {
            if (moves.containsAll(crossing.positions)) {
                _uiState.value = GameUiState.Winner(
                    player1Moves = player1Moves,
                    player2Moves = player2Moves,
                    statusText = "Player $player wins!!",
                    crossing = crossing
                )
                return
            }
        }
        if (player1Moves.size + player2Moves.size == 9) {
            // game is draw
            _uiState.value = GameUiState.Draw(
                player1Moves = player1Moves,
                player2Moves = player2Moves,
            )
        } else {
            val nextPlayer = if (isTurnOfPlayer1) 2 else 1
            _uiState.value = GameUiState.NextTurn(
                player1Moves = player1Moves,
                player2Moves = player2Moves,
                statusText = "Turn: Player $nextPlayer",
            )
        }
    }

    fun onRestartClick() {
        player1Moves.clear()
        player2Moves.clear()
        isTurnOfPlayer1 = true
        _uiState.value = GameUiState.Ready()
    }
}