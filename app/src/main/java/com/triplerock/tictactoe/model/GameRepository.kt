package com.triplerock.tictactoe.model

import com.triplerock.tictactoe.data.Move
import com.triplerock.tictactoe.data.Player1
import com.triplerock.tictactoe.data.Player2
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.utils.Logger

class GameRepository(private val firebase: Firebase) {

    var playingRoom: Room? = null
    private var currentPlayer = ""
    private var myName: String = ""
    private var isHost: Boolean = false

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
                isHost = true
                currentPlayer = Player1
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
            playingRoom = room
            currentPlayer = Player2
            onRoomJoined()
        }
    }

    fun setName(name: String) {
        myName = name
    }

    fun onMove(cell: Int) {
        if (playingRoom == null) {
            Logger.error("playing room not set")
            return
        }
        if (myName.isEmpty()) {
            Logger.error("my name not set")
            return
        }
        val move = Move(
            roomId = playingRoom!!.id,
            playerName = myName,
            player = currentPlayer,
            cell = cell,
        )
        firebase.submitMove(move, playingRoom!!) {
            // on move updated
        }
    }

    fun changeTurn(): String {
        val nextTurn = if (currentPlayer == Player1) Player2 else Player1
        playingRoom!!.nextTurn = nextTurn
        firebase.updateTurn(playingRoom!!)
        return nextTurn
    }

    fun listenForMoves(
        onMovesUpdate: (
            player1Moves: List<Int>,
            player2Moves: List<Int>,
        ) -> Unit,
    ) {
        if (playingRoom == null) {
            Logger.error("playing room is null")
            return
        }
        firebase.listenForTurns(playingRoom!!) { room ->
            playingRoom = room
        }
        firebase.listenForMoves(playingRoom!!.id) {
            val player1Moves = ArrayList<Int>()
            val player2Moves = ArrayList<Int>()
            for (move in it) {
                if (move.player == Player1) player1Moves.add(move.cell)
                else player2Moves.add(move.cell)
            }
            onMovesUpdate(player1Moves, player2Moves)
        }
    }

    fun clearMoves() {
        firebase.clearMoves(playingRoom!!)
    }

    fun resetGame() {
        clearMoves()
        playingRoom!!.nextTurn = Player1
        firebase.updateTurn(playingRoom!!)
    }
}