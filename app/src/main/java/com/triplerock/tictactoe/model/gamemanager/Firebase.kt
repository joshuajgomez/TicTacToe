package com.triplerock.tictactoe.model.gamemanager

import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.triplerock.tictactoe.data.PlayerO
import com.triplerock.tictactoe.data.PlayerX
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.utils.Logger

private const val COLLECTION_ROOMS = "rooms"

private const val keyPlayer2Name = "player2Name"
private const val keyNextTurn = "nextTurn"
private const val keyMoves = "moves"
private const val keyHistory = "history"

class Firebase : GameManager {

    private val callback: GameManager.Callback? = null

    private lateinit var roomsListener: ListenerRegistration

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

    fun findRooms(onRoomsFound: (rooms: List<Room>) -> Unit) {
        val roomRef = firestore.collection(COLLECTION_ROOMS)
            .whereEqualTo(keyPlayer2Name, "")
        roomsListener = roomRef.addSnapshotListener { snapshot, e ->
            if (e != null || snapshot == null) {
                Logger.error(e?.message.toString())
                return@addSnapshotListener
            }
            val rooms = arrayListOf<Room>()
            for (document in snapshot) {
                val room = document.toObject(Room::class.java)
                rooms.add(room)
            }
            Logger.debug("rooms found = ${rooms.size}")
            if (rooms.isNotEmpty()) {
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

    override fun listenUpdates(roomId: String, callback: GameManager.Callback) {
        Logger.debug("roomId = [${roomId}]")
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
            val roomUpdate = snapshot.toObject(Room::class.java)!!
            Logger.debug("snapshot:onTurnUpdate: $roomUpdate")
            callback.onRoomUpdate(roomUpdate)
        }
    }

    override fun onMove(room: Room) {
        Logger.debug("room = [${room}]")
        firestore.collection(COLLECTION_ROOMS).document(room.id)
            .update(
                keyNextTurn, room.nextTurn,
                keyMoves, room.moves,
                keyHistory, room.history,
            )
            .addOnSuccessListener {
                Logger.debug("success")
            }
            .addOnFailureListener {
                Logger.debug("fail")
            }
    }

    override fun clearMoves(room: Room) {
        Logger.info("room = [${room}]")
        val emptyMoves: HashMap<String, ArrayList<Int>> = hashMapOf(
            PlayerX to ArrayList(),
            PlayerO to ArrayList(),
        )
        firestore.collection(COLLECTION_ROOMS).document(room.id)
            .update(keyMoves, emptyMoves)
            .addOnSuccessListener { result ->
                Logger.debug("success")
                callback?.onRoomCleared()
            }
    }
}

