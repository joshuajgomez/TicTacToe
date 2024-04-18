package com.triplerock.tictactoe.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.triplerock.tictactoe.data.PlayerO
import com.triplerock.tictactoe.data.PlayerX
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.model.gamemanager.GameManager
import com.triplerock.tictactoe.model.gamemanager.LocalMultiPlayerGame
import com.triplerock.tictactoe.model.gamemanager.OnlineGame
import com.triplerock.tictactoe.model.gamemanager.SinglePlayerGame
import com.triplerock.tictactoe.ui.screens.game.Crossing
import com.triplerock.tictactoe.ui.screens.game.crossingList
import com.triplerock.tictactoe.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.java.KoinJavaComponent.get

sealed class GameUiState {
    data class Waiting(
        val statusText: String = "Setting up game",
    ) : GameUiState()

    data class NextTurn(
        val room: Room,
    ) : GameUiState()

    data class Draw(
        val room: Room,
    ) : GameUiState()

    data class Winner(
        val room: Room,
        val crossing: Crossing,
    ) : GameUiState()
}

const val navKeyRoomId = "roomId"
const val navKeyPlayer = "player"
const val navKeyMode = "mode"

const val modeSingle = "single_player"
const val modeMultiOnline = "multiplayer_online"
const val modeMultiLocal = "multiplayer_local"

class GameViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel(), GameManager.Callback {

    private val mode: String = checkNotNull(savedStateHandle[navKeyMode])
    var player: String = ""

    private lateinit var gameManager: GameManager

    private val playingRoom: MutableStateFlow<Room?> = MutableStateFlow(null)

    private val _uiState: MutableStateFlow<GameUiState> = MutableStateFlow(GameUiState.Waiting())
    val uiState: StateFlow<GameUiState> = _uiState

    init {
        var roomId = ""
        when (mode) {
            modeMultiOnline -> {
                gameManager = get(OnlineGame::class.java)
                roomId = checkNotNull(savedStateHandle[navKeyRoomId])
                player = checkNotNull(savedStateHandle[navKeyPlayer])
            }

            modeSingle -> {
                gameManager = get(SinglePlayerGame::class.java)
            }

            modeMultiLocal -> {
                gameManager = get(LocalMultiPlayerGame::class.java)
            }
        }
        Logger.debug("roomId = [${roomId}]")
        gameManager.listenUpdates(roomId, this)
    }

    private fun isReset(it: Room) = (it.moves[PlayerX]!!.isEmpty()
            && it.moves[PlayerO]!!.isEmpty())

    fun onCellClick(cell: Int) {
        Logger.debug("cell=$cell")
        val room = playingRoom.value
        room?.moves?.get(player)?.add(cell)
        room?.nextTurn = if (room?.nextTurn == PlayerX) PlayerO else PlayerX
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
        if (shouldUpdate) gameManager.onMove(room)
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

    override fun onRoomUpdate(room: Room) {
        val isStarting = playingRoom.value == null
        playingRoom.value = room
        if (isStarting
            || room.nextTurn == player
            || isReset(room)
        ) {
            // If nextTurn is other player, we already updated turn when cell was clicked
            Logger.verbose("room set. starting game")
            checkGameState()
        }
    }

    fun onRestartClick() {
        _uiState.value = GameUiState.Waiting()
        gameManager.clearMoves(playingRoom.value!!)
    }

    override fun onRoomCleared() {
        gameManager.onMove(playingRoom.value!!)
    }

}
