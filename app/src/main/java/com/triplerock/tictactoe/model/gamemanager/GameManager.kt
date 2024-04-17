package com.triplerock.tictactoe.model.gamemanager

import com.triplerock.tictactoe.data.Room

interface GameManager {
    fun listenUpdates(roomId: String, callback: GameManager.Callback)
    fun onMove(room: Room)
    fun clearMoves(room: Room)

    interface Callback {
        fun onRoomUpdate(room: Room)
        fun onRoomCleared()
    }
}

