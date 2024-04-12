package com.triplerock.tictactoe.data

import com.google.firebase.firestore.DocumentId

const val PlayerX = "X"
const val PlayerO = "O"

data class Room(
    @DocumentId
    var id: String = "",
    var name: String = "",
    var timeCreated: Long = 1712598251041,
    var status: String = "Setting up game",

    var nextTurn: String = PlayerX,

    var player1Name: String = "",
    var player2Name: String = "",

    val moves: HashMap<String, ArrayList<Int>> = hashMapOf(
        Pair(PlayerX, ArrayList()),
        Pair(PlayerO, ArrayList())
    ),

    var history: History = History(),
)

data class History(
    var xWins: Int = 0,
    var oWins: Int = 0,
    var draws: Int = 0,
)

data class Move(
    var id: String = "",
    var roomId: String = "",
    var playerName: String = "",
    var player: String = "",
    var cell: Int = 0,
)

val sampleNames = listOf(
    "pico",
    "mini",
    "mike",
    "tiny",
    "mario",
    "big",
    "max",
)

val sampleRoomNames = listOf(
    "pac-room",
    "money-bag",
    "middle-earth",
    "tiny-home",
    "max-people",
    "big-mac",
    "more-fun",
)
