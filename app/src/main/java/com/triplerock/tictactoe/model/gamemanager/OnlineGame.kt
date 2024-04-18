package com.triplerock.tictactoe.model.gamemanager

import com.triplerock.tictactoe.data.PlayerX
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.utils.Logger

class OnlineGame(
    private val firebase: Firebase,
    private val gameEngine: GameEngine,
) : GameManager {

    private lateinit var callback: GameManager.Callback
    private lateinit var player: String
    private lateinit var room: Room

    override fun setPlayer(player: String) {
        this.player = player
    }

    override fun listenUpdates(roomId: String, callback: GameManager.Callback) {
        Logger.debug("roomId = [${roomId}]")
        this.callback = callback
        firebase.listenUpdates(roomId) {
            room = it
            if (player == it.nextTurn) {
                callback.onGameUiStateChange(
                    gameEngine.nextState(room)
                )
            }
        }
    }

    override fun onMove(cell: Int) {
        Logger.debug("cell = [${cell}]")

        room.moves[player]?.add(cell)

        firebase.onMove(room)

        callback.onGameUiStateChange(
            gameEngine.nextState(room)
        )
    }

    override fun clearMoves() {
        Logger.debug("room = [${room}]")
        firebase.clearMoves(room)
    }
}