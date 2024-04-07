package com.triplerock.tictactoe.model

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.triplerock.tictactoe.data.Move
import com.triplerock.tictactoe.data.Player
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.utils.Logger

private const val COLLECTION_ROOMS = "rooms"
private const val COLLECTION_MOVES = "moves"

private const val keyPlayer2Name = "player2Name"
private const val keyIsTurnOfPlayer1 = "isTurnOfPlayer1"

private const val keyPlayerName = "player_name"
private const val keyCell = "cell"
private const val keyRoomId = "room_id"

class Firebase {

    private val firestore = Firebase.firestore

    fun createRoom(room: Room, onRoomCreated: (room: Room) -> Unit) {
        firestore.collection(COLLECTION_ROOMS)
            .add(room)
            .addOnSuccessListener {
                room.id = it.id
                onRoomCreated(room)
            }
            .addOnFailureListener {
                Logger.error(it.message.toString())
            }
    }

    fun waitForPlayers(room: Room, onPlayerJoining: (room: Room) -> Unit) {
        val roomRef = firestore.collection(COLLECTION_ROOMS).document(room.id)
        roomRef.addSnapshotListener { snapshot, e ->
            Logger.entry()
            if (e != null) {
                Logger.error(e.message.toString())
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Logger.debug("snapshot = ${snapshot.data}")
                if (snapshot[keyPlayer2Name].toString().isNotEmpty()) {
                    // Player 2 available. Start game
                    Logger.debug("Player 2 available. Starting game")
                    room.player2Name = snapshot[keyPlayer2Name].toString()
                    onPlayerJoining(room)
                } else {
                    // Player 2 not available
                    Logger.debug("Player 2 not available. Keep waiting")
                }
            } else {
                Logger.error("snapshot is null")
            }
        }
    }

    fun findRooms(onRoomsFound: (rooms: List<Room>) -> Unit) {
        val roomRef = firestore.collection(COLLECTION_ROOMS)
            .whereEqualTo(keyPlayer2Name, "")
        roomRef.addSnapshotListener { snapshot, e ->
            Logger.entry()
            if (e != null) {
                Logger.error(e.message.toString())
                return@addSnapshotListener
            }
            val rooms = getRooms(snapshot!!)
            if (rooms.isNotEmpty()) {
                onRoomsFound(rooms)
            }
        }
    }

    fun joinRoom(room: Room, onRoomJoined: () -> Unit) {
        firestore.collection(COLLECTION_ROOMS).document(room.id)
            .update(keyPlayer2Name, room.player2Name)
            .addOnSuccessListener {
                Logger.entry()
                onRoomJoined()
            }
            .addOnFailureListener {
                Logger.error(it.message.toString())
            }
    }

    fun submitMove(move: Move, room: Room) {
        firestore.collection(COLLECTION_MOVES)
            .add(move)
            .addOnSuccessListener {
                Logger.entry()
                updateTurn(room)
            }
            .addOnFailureListener {
                Logger.error(it.message.toString())
            }
    }

    private fun updateTurn(room: Room) {
        Logger.debug("room = [${room}]")
        firestore.collection(COLLECTION_ROOMS).document(room.id)
            .update(keyIsTurnOfPlayer1, !room.isTurnOfPlayer1)
            .addOnSuccessListener {
                Logger.entry()
            }
            .addOnFailureListener {
                Logger.entry()
            }
    }

    fun listenForMoves(roomId: String, onPlayerMoved: (moves: List<Move>) -> Unit) {
        val roomRef = firestore.collection(COLLECTION_MOVES)
            .whereEqualTo(keyRoomId, roomId)
        roomRef.addSnapshotListener { snapshot, e ->
            Logger.entry()
            if (e != null) {
                Logger.error(e.message.toString())
                return@addSnapshotListener
            }
            val moves = getMoves(snapshot!!)
            if (moves.isNotEmpty()) {
                onPlayerMoved(moves)
            }
        }
    }

    fun listenForTurns(room: Room, onTurnUpdate: (room: Room) -> Unit) {
        val roomRef = firestore.collection(COLLECTION_ROOMS)
            .whereEqualTo(keyRoomId, room.id)
        roomRef.addSnapshotListener { snapshot, e ->
            Logger.entry()
            if (e != null) {
                Logger.error(e.message.toString())
                return@addSnapshotListener
            }
            if (snapshot == null || snapshot.isEmpty) {
                Logger.error("snapshot is null / empty")
                return@addSnapshotListener
            }
            val isTurnOfPlayer1 = snapshot.first().getBoolean(keyIsTurnOfPlayer1)
            room.isTurnOfPlayer1 = isTurnOfPlayer1!!
            onTurnUpdate(room)
        }
    }

    fun clearMoves(playingRoom: Room) {
        firestore.collection(COLLECTION_MOVES)
            .whereEqualTo(keyRoomId, playingRoom.id)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val batch = firestore.batch()
                    for (document in value) {
                        batch.delete(document.reference)
                    }
                    batch.commit()
                }
            }
    }
}

private fun getRooms(result: QuerySnapshot): List<Room> {
    val rooms = ArrayList<Room>()
    for (document in result) {
        val room = document.toObject(Room::class.java)
        room.id = document.id
        rooms.add(room)
    }
    return rooms
}

private fun getMoves(result: QuerySnapshot): List<Move> {
    val moves = ArrayList<Move>()
    for (document in result) {
        val move = document.toObject(Move::class.java)
        moves.add(move)
    }
    return moves
}