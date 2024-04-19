package com.triplerock.tictactoe.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.model.gamemanager.GameManager
import com.triplerock.tictactoe.model.gamemanager.LocalMultiPlayerGame
import com.triplerock.tictactoe.model.gamemanager.OnlineGame
import com.triplerock.tictactoe.model.gamemanager.SinglePlayerGame
import com.triplerock.tictactoe.ui.screens.game.Crossing
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

    val mode: String = checkNotNull(savedStateHandle[navKeyMode])
    var player: String = checkNotNull(savedStateHandle[navKeyPlayer])

    private var gameManager: GameManager

    private val _uiState: MutableStateFlow<GameUiState> = MutableStateFlow(GameUiState.Waiting())
    val uiState: StateFlow<GameUiState> = _uiState

    init {
        Logger.debug("mode = $mode")
        gameManager = when (mode) {
            modeMultiOnline -> get(OnlineGame::class.java)
            modeMultiLocal -> get(LocalMultiPlayerGame::class.java)
            else -> get(SinglePlayerGame::class.java)
        }
        gameManager.setPlayer(player)
        val roomId: String = checkNotNull(savedStateHandle[navKeyRoomId])
        gameManager.listenUpdates(roomId, this)
    }

    fun onCellClick(cell: Int) {
        Logger.debug("cell=$cell")
        gameManager.onMove(cell)
    }

    override fun onGameUiStateChange(uiState: GameUiState) {
        Logger.debug("uiState = [${uiState}]")
        _uiState.value = uiState
    }

    override fun updatePlayer(player: String) {
        this.player = player
    }

    fun onRestartClick() {
        _uiState.value = GameUiState.Waiting()
        gameManager.clearMoves()
    }

}
