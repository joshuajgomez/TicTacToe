package com.triplerock.tictactoe.model

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
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
private const val keyNextTurn = "nextTurn"

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

    private lateinit var playersListener: ListenerRegistration

    fun waitForPlayers(room: Room, onPlayerJoining: (room: Room) -> Unit) {
        val roomRef = firestore.collection(COLLECTION_ROOMS).document(room.id)
        playersListener = roomRef.addSnapshotListener() { snapshot, e ->
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
                    playersListener.remove()
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

    private lateinit var roomsListener: ListenerRegistration

    fun findRooms(onRoomsFound: (rooms: List<Room>) -> Unit) {
        val roomRef = firestore.collection(COLLECTION_ROOMS)
            .whereEqualTo(keyPlayer2Name, "")
        roomsListener = roomRef.addSnapshotListener { snapshot, e ->
            Logger.entry()
            if (e != null) {
                Logger.error(e.message.toString())
                return@addSnapshotListener
            }
            val rooms = getRooms(snapshot!!)
            if (rooms.isNotEmpty()) {
                roomsListener.remove()
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

    fun submitMove(move: Move, room: Room, onMoveUpdated: () -> Unit) {
        Logger.debug("move = [${move}], room = [${room}]")
        firestore.collection(COLLECTION_MOVES)
            .add(move)
            .addOnSuccessListener {
                Logger.entry()
                onMoveUpdated()
            }
            .addOnFailureListener {
                Logger.error(it.message.toString())
            }
    }

    fun updateTurn(room: Room) {
        Logger.debug("room = [${room}]")
        firestore.collection(COLLECTION_ROOMS).document(room.id)
            .update(keyIsTurnOfPlayer1, room.isTurnOfPlayer1)
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
            Logger.debug("onMoveUpdate: $moves")
            if (moves.isNotEmpty()) {
                onPlayerMoved(moves)
            }
        }
    }

    fun listenForTurns(room: Room, onTurnUpdate: (room: Room) -> Unit) {
        Logger.debug("room = [${room}], onTurnUpdate = [${onTurnUpdate}]")
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
            val roomUpdate = snapshot.first().toObject(Room::class.java)
            Logger.debug("onTurnUpdate: $roomUpdate")
            onTurnUpdate(roomUpdate)
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