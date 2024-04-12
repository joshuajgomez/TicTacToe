package com.triplerock.tictactoe.model

import com.triplerock.tictactoe.data.Move
import com.triplerock.tictactoe.data.Player1
import com.triplerock.tictactoe.data.Player2
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.data.sampleNames
import com.triplerock.tictactoe.utils.SharedPrefUtil
import com.triplerock.tictactoe.utils.Logger

class GameRepository(
    private val firebase: Firebase,
    private val sharedPrefUtil: SharedPrefUtil,
) {

    private var myName: String

    init {
        if (!sharedPrefUtil.isNameSet()) {
            sharedPrefUtil.setName(sampleNames.random())
        }
        myName = sharedPrefUtil.getName()
    }

    private var currentPlayer = ""
    private var isHost: Boolean = false

    fun createRoom(
        roomName: String,
        onRoomCreated: (room: Room) -> Unit,
        onPlayerJoined: (room: Room) -> Unit,
    ) {
        Logger.entry()
        val room = Room(
            name = roomName,
            player1Name = myName,
            player2Name = "",
            timeCreated = System.currentTimeMillis()
        )
        firebase.createRoom(room) { it ->
            // room created
            onRoomCreated(it)
            firebase.waitForPlayers(it) {
                // on players joining
                isHost = true
                currentPlayer = Player1
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
        if (myName.isEmpty()) {
            Logger.error("myName not set")
            return
        }
        room.player2Name = myName
        firebase.joinRoom(room) {
            // room joined
            currentPlayer = Player2
            onRoomJoined()
        }
    }

    fun setName(name: String) {
        myName = name
        sharedPrefUtil.setName(name)
    }

    fun onMove(cell: Int, roomId: String) {
        val move = Move(
            roomId = roomId,
            playerName = myName,
            player = currentPlayer,
            cell = cell,
        )
        firebase.submitMove(move) {
            // on move updated
        }
    }

    fun listenForMoves(
        roomId: String,
        onMovesUpdate: (
            player1Moves: List<Int>,
            player2Moves: List<Int>,
        ) -> Unit,
    ) {
        firebase.listenForMoves(roomId) {
            // on player moved
            val player1Moves = ArrayList<Int>()
            val player2Moves = ArrayList<Int>()
            for (move in it) {
                if (move.player == Player1) player1Moves.add(move.cell)
                else player2Moves.add(move.cell)
            }
            onMovesUpdate(player1Moves, player2Moves)
        }
    }

    fun listenForTurnUpdates(roomId: String, onTurnChange: (room: Room) -> Unit) {
        firebase.listenForTurns(roomId) { room ->
            onTurnChange(room)
        }
    }

    fun updateTurn(room: Room) {
        firebase.updateTurn(room)
    }

    fun getName(): String {
        return myName
    }

    fun resetGame(room: Room, onResetComplete: () -> Unit) {
        firebase.clearMoves(playingRoom = room) {
            // on reset complete
            onResetComplete()
        }
    }
}