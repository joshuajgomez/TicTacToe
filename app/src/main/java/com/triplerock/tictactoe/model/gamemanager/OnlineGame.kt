package com.triplerock.tictactoe.model.gamemanager

import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.utils.Logger

class OnlineGame(private val gameManager: GameManager) : GameManager {

    private lateinit var callback: GameManager.Callback

    override fun listenUpdates(roomId: String, callback: GameManager.Callback) {
        Logger.debug("roomId = [${roomId}]")
        this.callback = callback
        gameManager.listenUpdates(roomId, callback)
    }

    override fun onMove(room: Room) {
        Logger.debug("room = [${room}]")
        gameManager.onMove(room)
    }

    override fun clearMoves(room: Room) {
        Logger.debug("room = [${room}]")
        gameManager.clearMoves(room)
    }
}