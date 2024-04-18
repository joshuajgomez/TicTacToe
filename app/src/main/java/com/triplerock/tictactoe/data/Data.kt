package com.triplerock.tictactoe.data

import com.google.firebase.firestore.DocumentId

const val PlayerX = "X"
const val PlayerO = "O"

val emptyMoves = hashMapOf<String, ArrayList<Int>>(
    PlayerX to arrayListOf(),
    PlayerO to arrayListOf(),
)

data class Room(
    @DocumentId
    var id: String = "",
    var name: String = "",
    var timeCreated: Long = 1712598251041,
    var status: String = "Setting up game",

    var nextTurn: String = PlayerX,

    var player1Name: String = "",
    var player2Name: String = "",

    var moves: HashMap<String, ArrayList<Int>> = emptyMoves,

    var history: History = History(),
) {
    override fun equals(other: Any?): Boolean {
        return false
    }

    fun changeTurn() {
        nextTurn = if (nextTurn == PlayerX) PlayerO else PlayerX
    }

    fun isEmptyMoves(): Boolean {
        return moves[PlayerX]!!.isEmpty() && moves[PlayerO]!!.isEmpty()
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + timeCreated.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + nextTurn.hashCode()
        result = 31 * result + player1Name.hashCode()
        result = 31 * result + player2Name.hashCode()
        result = 31 * result + moves.hashCode()
        result = 31 * result + history.hashCode()
        return result
    }
}

data class History(
    var xWins: Int = 0,
    var oWins: Int = 0,
    var draws: Int = 0,
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
