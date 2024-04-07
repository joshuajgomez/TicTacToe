package com.triplerock.tictactoe.data

data class Room(
    var id: String = "",
    var name: String = "",
    var player1Name: String = "",
    var player2Name: String = "",
)

data class Player(
    var id: String = "",
    var name: String = "",
)

data class Move(
    var id: String = "",
    var roomId: String = "",
    var playerId: String = "",
    var cell: Int = 0,
)
