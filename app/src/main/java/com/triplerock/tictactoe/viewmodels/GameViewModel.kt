package com.triplerock.tictactoe.viewmodels

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import com.triplerock.tictactoe.model.GameRepository
import com.triplerock.tictactoe.utils.Logger
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

class GameViewModel(private val gameRepository: GameRepository) : ViewModel() {

    private var isTurnOfPlayer1 = true

    private var player1Moves: List<Int> = ArrayList()
    private var player2Moves: List<Int> = ArrayList()

    private val _uiState: MutableStateFlow<GameUiState> = MutableStateFlow(GameUiState.Ready())
    val uiState: StateFlow<GameUiState> = _uiState

    fun startGame() {
        gameRepository.listenForMoves(
            onMovesUpdate = { moves1, moves2 ->
                Logger.entry()
                player1Moves = moves1
                player2Moves = moves2
                checkGameState()
            },
        )
    }

    private fun checkGameState() {
        val turnOfPlayer1 = gameRepository.playingRoom!!.isTurnOfPlayer1
        Logger.debug("turnOfPlayer1 = $turnOfPlayer1")
        if (turnOfPlayer1) {
            checkState(1, player1Moves)
        } else {
            checkState(2, player2Moves)
        }
    }

    fun onCellClick(cell: Int) {
        Logger.debug("cell=$cell")
        gameRepository.onMove(cell)
    }

    private fun checkState(player: Int, moves: List<Int>): Boolean {
        for (crossing in crossingList) {
            if (moves.containsAll(crossing.positions)) {
                _uiState.value = GameUiState.Winner(
                    player1Moves = player1Moves,
                    player2Moves = player2Moves,
                    statusText = "Player $player wins!!",
                    crossing = crossing
                )
                return true
            }
        }
        if (player1Moves.size + player2Moves.size == 9) {
            // game is draw
            _uiState.value = GameUiState.Draw(
                player1Moves = player1Moves,
                player2Moves = player2Moves,
            )
            return true
        } else {
            val nextPlayer = if (isTurnOfPlayer1) 2 else 1
            _uiState.value = GameUiState.NextTurn(
                player1Moves = player1Moves,
                player2Moves = player2Moves,
                statusText = "Turn: Player $nextPlayer",
            )
        }
        return false
    }

    fun onRestartClick() {
        gameRepository.clearMoves()
        isTurnOfPlayer1 = true
        _uiState.value = GameUiState.Ready()
    }
}