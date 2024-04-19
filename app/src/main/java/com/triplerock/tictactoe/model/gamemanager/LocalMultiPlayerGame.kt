package com.triplerock.tictactoe.model.gamemanager

import com.triplerock.tictactoe.data.PlayerO
import com.triplerock.tictactoe.data.PlayerX
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.data.sampleRoomNames
import com.triplerock.tictactoe.utils.Logger

class LocalMultiPlayerGame(
    private val gameEngine: GameEngine,
) : GameManager {

    private lateinit var callback: GameManager.Callback
    private lateinit var player: String
    private lateinit var room: Room

    override fun setPlayer(player: String) {
        Logger.debug("player = [${player}]")
        this.player = player
    }

    override fun listenUpdates(roomId: String, callback: GameManager.Callback) {
        Logger.debug("roomId = [${roomId}]")
        this.callback = callback

        room = Room(
            name = sampleRoomNames.random(),
            player1Name = "Player 1",
            player2Name = "Player 2",
            nextTurn = PlayerX,
        )

        callback.onGameUiStateChange(
            gameEngine.nextState(room)
        )
    }

    override fun onMove(cell: Int) {
        Logger.debug("cell = [${cell}]")
        room.moves[PlayerX]?.add(cell)
        callback.updatePlayer(if (player == PlayerX) PlayerO else PlayerX)
        callback.onGameUiStateChange(
            gameEngine.nextState(room)
        )
    }

    override fun clearMoves() {
        Logger.entry()
        room.moves[PlayerX]?.clear()
        room.moves[PlayerO]?.clear()
        callback.onGameUiStateChange(
            gameEngine.nextState(room)
        )
    }

}