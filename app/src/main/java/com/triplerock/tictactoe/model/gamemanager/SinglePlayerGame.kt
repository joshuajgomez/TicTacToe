package com.triplerock.tictactoe.model.gamemanager

import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.viewmodels.GameViewModel

class SinglePlayerGame : GameManager {

    private lateinit var callback: GameManager.Callback

    override fun listenUpdates(roomId: String, callback: GameManager.Callback) {
        this.callback = callback
    }

    override fun onMove(room: Room) {
        TODO("Not yet implemented")
    }

    override fun clearMoves(room: Room) {
        TODO("Not yet implemented")
    }
}