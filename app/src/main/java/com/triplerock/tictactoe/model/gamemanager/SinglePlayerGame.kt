package com.triplerock.tictactoe.model.gamemanager

import com.triplerock.tictactoe.data.PlayerO
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.ui.screens.game.crossingList
import com.triplerock.tictactoe.viewmodels.GameViewModel

class SinglePlayerGame : GameManager {

    private lateinit var callback: GameManager.Callback

    override fun listenUpdates(roomId: String, callback: GameManager.Callback) {
        this.callback = callback
    }

    override fun onMove(room: Room) {
        val moves = room.moves[PlayerO]!!
        crossingList.forEach { crossing ->
            var matchingCount = 0
            crossing.positions.forEach {
                if (moves.contains(it)) matchingCount++
            }
        }
        moves.add(0)
        callback.onRoomUpdate(room)
    }

    override fun clearMoves(room: Room) {
        TODO("Not yet implemented")
    }
}