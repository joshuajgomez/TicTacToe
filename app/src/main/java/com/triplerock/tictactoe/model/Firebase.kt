package com.triplerock.tictactoe.model

import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.triplerock.tictactoe.data.Move
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.utils.Logger

private const val COLLECTION_ROOMS = "rooms"
private const val COLLECTION_MOVES = "moves"

private const val keyPlayer2Name = "player2Name"
private const val keyNextTurn = "nextTurn"

private const val keyRoomId = "roomId"

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
            if (e != null) {
                Logger.error(e.message.toString())
                return@addSnapshotListener
            }
            val rooms = getRooms(snapshot!!)
            if (rooms.isNotEmpty()) {
                Logger.debug("rooms found = ${rooms.size}")
                onRoomsFound(rooms)
            }
        }
    }

    fun joinRoom(room: Room, onRoomJoined: () -> Unit) {
        roomsListener.remove()
        firestore.collection(COLLECTION_ROOMS).document(room.id)
            .update(keyPlayer2Name, room.player2Name)
            .addOnSuccessListener {
                Logger.debug("success")
                onRoomJoined()
            }
            .addOnFailureListener {
                Logger.error("failure: ${it.message.toString()}")
            }
    }

    fun submitMove(move: Move, onMoveUpdated: () -> Unit) {
        Logger.debug("move = [${move}]")
        firestore.collection(COLLECTION_MOVES)
            .add(move)
            .addOnSuccessListener {
                Logger.debug("success")
                onMoveUpdated()
            }
            .addOnFailureListener {
                Logger.error(it.message.toString())
            }
    }

    fun updateTurn(room: Room) {
        Logger.debug("room = [${room}]")
        firestore.collection(COLLECTION_ROOMS).document(room.id)
            .update(keyNextTurn, room.nextTurn)
            .addOnSuccessListener {
                Logger.debug("success")
            }
            .addOnFailureListener {
                Logger.debug("fail")
            }
    }

    fun listenForMoves(roomId: String, onPlayerMoved: (moves: List<Move>) -> Unit) {
        val roomRef = firestore.collection(COLLECTION_MOVES)
            .whereEqualTo(keyRoomId, roomId)
        roomRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Logger.error(e.message.toString())
                return@addSnapshotListener
            }
            val moves = getMoves(snapshot!!)
            Logger.debug("onMoveUpdate: $moves")
            onPlayerMoved(moves)
        }
    }

    fun listenForTurns(roomId: String, onTurnUpdate: (room: Room) -> Unit) {
        Logger.debug("roomId = [${roomId}], onTurnUpdate = [${onTurnUpdate}]")
        val roomRef = firestore.collection(COLLECTION_ROOMS).document(roomId)
        roomRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Logger.error(e.message.toString())
                return@addSnapshotListener
            }
            if (snapshot == null || snapshot.data == null) {
                Logger.warn("snapshot is null")
                return@addSnapshotListener
            }
            val roomUpdate = snapshot.toObject(Room::class.java)
            Logger.debug("snapshot:onTurnUpdate: $roomUpdate")
            onTurnUpdate(roomUpdate!!)
        }
    }

    fun clearMoves(playingRoom: Room, onResetComplete: () -> Unit) {
        firestore.collection(COLLECTION_MOVES)
            .whereEqualTo(keyRoomId, playingRoom.id)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Logger.error(error.message.toString())
                    return@addSnapshotListener
                }
                if (value == null) {
                    Logger.error("value is null")
                    return@addSnapshotListener
                }
                val batch = firestore.batch()
                for (document in value) {
                    batch.delete(document.reference)
                }
                batch.commit().addOnSuccessListener {
                    Logger.debug("success")
                    onResetComplete()
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