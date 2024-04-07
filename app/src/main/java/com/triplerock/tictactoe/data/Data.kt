package com.triplerock.tictactoe.data

import com.google.firebase.firestore.DocumentId

data class Room(
    @DocumentId
    var id: String = "",
    var name: String = "",
    var player1Name: String = "",
    var player2Name: String = "",
    var isTurnOfPlayer1: Boolean = true,
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
    var cell: Int = 0,
    var isHost: Boolean = false,
)
