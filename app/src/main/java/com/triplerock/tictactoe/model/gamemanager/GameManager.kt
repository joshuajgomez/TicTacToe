package com.triplerock.tictactoe.model.gamemanager

import com.triplerock.tictactoe.viewmodels.GameUiState

interface GameManager {

    fun setPlayer(player: String)
    fun listenUpdates(roomId: String, callback: Callback)
    fun onMove(cell: Int)
    fun clearMoves()

    interface Callback {
        fun onGameUiStateChange(uiState: GameUiState)
    }
}

