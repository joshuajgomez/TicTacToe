package com.triplerock.tictactoe.model

import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.utils.Logger

class GameRepository(private val firebase: Firebase) {

    private var playingRoom: Room? = null
    private var myName: String = ""

    fun createRoom(
        roomName: String,
        onRoomCreated: (room: Room) -> Unit,
        onPlayerJoined: (player2Name: String) -> Unit,
    ) {
        Logger.entry()
        val room = Room(name = roomName, player1Name = myName, player2Name = "")
        firebase.createRoom(room) { it ->
            // room created
            onRoomCreated(it)
            firebase.waitForPlayers(it) {
                // on players joining
                playingRoom = it
                onPlayerJoined(it.player2Name)
            }
        }
    }

    fun findRooms(
        onRoomFound: (rooms: List<Room>) -> Unit,
    ) {
        Logger.entry()
        firebase.findRooms {
            onRoomFound(it)
        }
    }

    fun joinRoom(
        room: Room,
        onRoomJoined: () -> Unit,
    ) {
        Logger.entry()
        if (myName.isEmpty()) {
            Logger.error("myName not set")
            return
        }
        room.player2Name = myName
        firebase.joinRoom(room) {
            // room joined
            onRoomJoined()
        }
    }

    fun setName(name: String) {
        myName = name
    }
}