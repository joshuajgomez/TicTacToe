package com.triplerock.tictactoe.model.gamemanager

import android.os.Handler
import android.os.Looper
import com.triplerock.tictactoe.data.PlayerO
import com.triplerock.tictactoe.data.PlayerX
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.data.emptyMoves
import com.triplerock.tictactoe.data.sampleRoomNames
import com.triplerock.tictactoe.ui.screens.game.Crossing
import com.triplerock.tictactoe.utils.SharedPrefUtil

const val invalid = -1

class SinglePlayerGame(
    private val gameEngine: GameEngine,
    private val sharedPrefUtil: SharedPrefUtil,
) : GameManager {

    private lateinit var callback: GameManager.Callback

    private lateinit var room: Room
    private lateinit var player: String

    override fun setPlayer(player: String) {
        this.player = player
    }

    override fun listenUpdates(roomId: String, callback: GameManager.Callback) {
        this.callback = callback

        room = Room(
            name = sampleRoomNames.random(),
            player1Name = sharedPrefUtil.getName(),
            player2Name = "bot",
            nextTurn = PlayerX,
        )

        callback.onGameUiStateChange(
            gameEngine.nextState(room)
        )
    }

    data class CrossMatch(
        val crossing: Crossing,
        val matches: Int,
    ) {
        override fun toString(): String {
            return "CrossMatch(crossing=${crossing.positions}, matches=$matches)"
        }
    }

    override fun onMove(cell: Int) {
        room.moves[PlayerX]?.add(cell)

        callback.onGameUiStateChange(
            gameEngine.nextState(room)
        )

        Handler(Looper.getMainLooper()).postDelayed({
            makeNextMove()
        }, 1000)
    }

    private fun makeNextMove() {
        val nextMove = gameEngine.nextMove(room)
        println("nextMove = $nextMove")
        if (nextMove != invalid) {
            room.moves[PlayerO]?.add(nextMove)
            callback.onGameUiStateChange(
                gameEngine.nextState(room)
            )
        }
    }


    override fun clearMoves() {
        room.moves[PlayerX]?.clear()
        room.moves[PlayerO]?.clear()
        callback.onGameUiStateChange(
            gameEngine.nextState(room)
        )
    }

}