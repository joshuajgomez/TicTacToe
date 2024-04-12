package com.triplerock.tictactoe.model

import com.triplerock.tictactoe.data.Move
import com.triplerock.tictactoe.data.PlayerO
import com.triplerock.tictactoe.data.PlayerX
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.data.sampleNames
import com.triplerock.tictactoe.utils.SharedPrefUtil
import com.triplerock.tictactoe.utils.Logger

class GameRepository(
    private val firebase: Firebase,
    private val sharedPrefUtil: SharedPrefUtil,
) {

    private var playerName: String

    init {
        if (!sharedPrefUtil.isNameSet()) {
            sharedPrefUtil.setName(sampleNames.random())
        }
        playerName = sharedPrefUtil.getName()
    }

    private var player = ""

    fun createRoom(
        roomName: String,
        onRoomCreated: (room: Room) -> Unit,
        onPlayerJoined: (room: Room) -> Unit,
    ) {
        Logger.entry()
        val room = Room(
            name = roomName,
            player1Name = playerName,
            timeCreated = System.currentTimeMillis(),
        )
        firebase.createRoom(room) { it ->
            // room created
            onRoomCreated(it)
            firebase.waitForPlayers(it) {
                // on players joining
                player = PlayerX
                onPlayerJoined(it)
            }
        }
    }

    fun findRooms(
        onRoomFound: (rooms: List<Room>) -> Unit,
    ) {
        Logger.entry()
        firebase.findRooms {
            onRoomFound(
                it.sortedByDescending { room ->
                    room.timeCreated
                })
        }
    }

    fun joinRoom(
        room: Room,
        onRoomJoined: () -> Unit,
    ) {
        Logger.entry()
        if (playerName.isEmpty()) {
            Logger.error("myName not set")
            return
        }
        room.player2Name = playerName
        firebase.joinRoom(room) {
            // room joined
            player = PlayerO
            onRoomJoined()
        }
    }

    fun setName(name: String) {
        playerName = name
        sharedPrefUtil.setName(name)
    }

    fun listenForRoomUpdates(roomId: String, onTurnChange: (room: Room) -> Unit) {
        firebase.listenForTurns(roomId) { room ->
            onTurnChange(room)
        }
    }

    fun updateTurn(room: Room) {
        firebase.updateTurn(room)
    }

    fun getName(): String {
        return playerName
    }

    fun resetGame(room: Room, onResetComplete: () -> Unit) {
        firebase.clearMoves(playingRoom = room) {
            // on reset complete
            onResetComplete()
        }
    }
}