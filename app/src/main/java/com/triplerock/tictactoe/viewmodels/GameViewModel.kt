package com.triplerock.tictactoe.viewmodels

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.triplerock.tictactoe.data.Player1
import com.triplerock.tictactoe.data.Room
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
    data class Waiting(
        val statusText: String = "Setting up game",
    ) : GameUiState()

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

class GameViewModel(
    savedStateHandle: SavedStateHandle,
    private val gameRepository: GameRepository
) : ViewModel() {

    private val roomId: String = checkNotNull(savedStateHandle["roomId"])

    private var player1Moves: List<Int> = ArrayList()
    private var player2Moves: List<Int> = ArrayList()

    private var playingRoom: MutableStateFlow<Room?> = MutableStateFlow(null)

    private val _uiState: MutableStateFlow<GameUiState> = MutableStateFlow(GameUiState.Waiting())
    val uiState: StateFlow<GameUiState> = _uiState

    init {
        gameRepository.listenForTurnUpdates(roomId) {
            if (playingRoom.value == null)
                _uiState.value = GameUiState.Ready()
            playingRoom.value = it
        }
        gameRepository.listenForMoves(
            roomId = roomId,
            onMovesUpdate = { moves1, moves2 ->
                Logger.entry()
                player1Moves = moves1
                player2Moves = moves2
                checkGameState()
            },
        )
    }

    private fun checkGameState() {
        val nextTurn = playingRoom.value!!.nextTurn
        Logger.debug("turnOfPlayer1 = $nextTurn")
        if (nextTurn == Player1) {
            checkState(nextTurn, player1Moves)
        } else {
            checkState(nextTurn, player2Moves)
        }
    }

    fun onCellClick(cell: Int) {
        Logger.debug("cell=$cell")
        gameRepository.onMove(cell, roomId)
    }

    private fun checkState(player: String, moves: List<Int>): Boolean {
        for (crossing in crossingList) {
            if (moves.containsAll(crossing.positions)) {
                _uiState.value = GameUiState.Winner(
                    player1Moves = player1Moves,
                    player2Moves = player2Moves,
                    statusText = "$player wins!!",
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
            val nextPlayer = gameRepository.updateTurn(playingRoom.value!!)
            _uiState.value = GameUiState.NextTurn(
                player1Moves = player1Moves,
                player2Moves = player2Moves,
                statusText = "Turn: $nextPlayer",
            )
        }
        return false
    }

    fun onRestartClick() {
        _uiState.value = GameUiState.Ready()
    }
}