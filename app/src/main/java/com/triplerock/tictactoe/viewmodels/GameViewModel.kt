package com.triplerock.tictactoe.viewmodels

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.triplerock.tictactoe.data.PlayerO
import com.triplerock.tictactoe.data.PlayerX
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.model.GameRepository
import com.triplerock.tictactoe.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Crossing(val start: Offset, val end: Offset, val positions: List<Int>)

const val rectOffset = 560f
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

    data class NextTurn(
        val room: Room
    ) : GameUiState()

    data class Draw(
        val room: Room
    ) : GameUiState()

    data class Winner(
        val room: Room,
        val crossing: Crossing,
    ) : GameUiState()
}

const val navKeyRoomId = "roomId"
const val navKeyPlayer = "player"

class GameViewModel(
    savedStateHandle: SavedStateHandle,
    private val gameRepository: GameRepository,
) : ViewModel() {

    private val roomId: String = checkNotNull(savedStateHandle[navKeyRoomId])
    val player: String = checkNotNull(savedStateHandle[navKeyPlayer])

    val playerName: String = gameRepository.getName()

    val playingRoom: MutableStateFlow<Room?> = MutableStateFlow(null)

    private val _uiState: MutableStateFlow<GameUiState> = MutableStateFlow(GameUiState.Waiting())
    val uiState: StateFlow<GameUiState> = _uiState

    init {
        Logger.debug("roomId = [${roomId}]")
        gameRepository.listenForRoomUpdates(roomId) {
            val isStarting = playingRoom.value == null
            playingRoom.value = it
            if (isStarting || it.nextTurn == player) {
                // If nextTurn is other player, we already updated turn when cell was clicked
                Logger.verbose("room set. starting game")
                checkGameState()
            }
        }
    }

    fun onCellClick(cell: Int) {
        Logger.debug("cell=$cell")
        val room = playingRoom.value
        room?.moves?.get(player)?.add(cell)
        room?.nextTurn = if (room?.nextTurn == PlayerX) PlayerO else PlayerX
        playingRoom.value = room
        checkGameState(true)
    }

    private fun checkGameState(shouldUpdate: Boolean = false) {
        val room = playingRoom.value!!
        Logger.debug("nextTurn = $room")
        if (isDraw()) {
            // game draw
            if (shouldUpdate) room.history.draws++
        } else if (isWon(room.moves[PlayerX]!!)) {
            // game won by playerX
            if (shouldUpdate) room.history.xWins++
        } else if (isWon(room.moves[PlayerO]!!)) {
            // game won by playerO
            if (shouldUpdate) room.history.oWins++
        } else {
            // change turn
            Logger.info("changing turn")
            room.status = "Turn: ${room.nextTurn}"
            _uiState.value = GameUiState.NextTurn(room)
        }
        if (shouldUpdate) gameRepository.updateTurn(room)
    }

    private fun isWon(moves: List<Int>): Boolean {
        val room = playingRoom.value!!
        for (crossing in crossingList) {
            if (moves.containsAll(crossing.positions)) {
                room.status = "$player wins!!"
                _uiState.value = GameUiState.Winner(
                    room = room,
                    crossing = crossing
                )
                return true
            }
        }
        return false
    }

    private fun isDraw(): Boolean {
        val room = playingRoom.value!!
        if (room.moves[PlayerX]!!.size + room.moves[PlayerO]!!.size == 9) {
            // game is draw
            room.status = "Draw :|"
            _uiState.value = GameUiState.Draw(room)
            return true
        }
        return false
    }

    fun onRestartClick() {
        _uiState.value = GameUiState.Waiting()
        gameRepository.resetGame(playingRoom.value!!) {
            // on reset complete
            gameRepository.updateTurn(playingRoom.value!!)
        }
    }

}
