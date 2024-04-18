package com.triplerock.tictactoe.model.gamemanager

import com.triplerock.tictactoe.utils.Logger

class LocalMultiPlayerGame : GameManager {

    private lateinit var callback: GameManager.Callback

    override fun setPlayer(player: String) {
        Logger.debug("player = [${player}]")
    }

    override fun listenUpdates(roomId: String, callback: GameManager.Callback) {
        Logger.debug("roomId = [${roomId}]")
        this.callback = callback
    }

    override fun onMove(cell: Int) {
        Logger.debug("cell = [${cell}]")
    }

    override fun clearMoves() {
        Logger.entry()
    }

}