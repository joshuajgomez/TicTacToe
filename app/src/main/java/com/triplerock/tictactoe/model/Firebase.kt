package com.triplerock.tictactoe.model

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.triplerock.tictactoe.data.Player
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.utils.Logger

private const val COLLECTION_ROOMS = "rooms"
private const val COLLECTION_MOVES = "moves"

private const val keyRoomName = "room_name"
private const val keyPlayer1Name = "player1_name"
private const val keyPlayer2Name = "player2_name"

private const val keyPlayerName = "player_name"

class Firebase {

    private val firestore = Firebase.firestore

    fun createRoom(room: Room, onRoomCreated: (room: Room) -> Unit) {
        firestore.collection(COLLECTION_ROOMS)
            .add(createDoc(room))
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
                if (snapshot.contains(keyPlayer2Name)) {
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
            val rooms = getRooms(snapshot)
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
}

private fun createDoc(room: Room): HashMap<String, Any> {
    return hashMapOf(
        keyRoomName to room.name,
        keyPlayer1Name to room.player1Name,
        keyPlayer2Name to room.player2Name,
    )
}

private fun createPlayer(snapshot: DocumentSnapshot): Player {
    return Player(
        id = snapshot.id,
        name = snapshot[keyPlayerName].toString(),
    )
}

private fun getRooms(result: QuerySnapshot?): List<Room> {
    val rooms = ArrayList<Room>()
    for (document in result!!) {
        val room = Room(
            id = document.id,
            name = document[keyRoomName].toString(),
            player1Name = document[keyPlayer1Name].toString(),
            player2Name = document[keyPlayer2Name].toString(),
        )
        rooms.add(room)
    }
    return rooms
}