package com.triplerock.tictactoe.data

import com.google.firebase.firestore.DocumentId

const val Player1 = "Player1"
const val Player2 = "Player2"

data class Room(
    @DocumentId
    var id: String = "",
    var name: String = "",
    var player1Name: String = "",
    var player2Name: String = "",
    var nextTurn: String = Player1,
)

data class Player(
    var id: String = "",
    var name: String = "",
)

data class Move(
    @DocumentId
    var id: String = "",
    var roomId: String = "",
    var playerName: String = "",
    var player: String = "",
    var cell: Int = 0,
)
