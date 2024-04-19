package com.triplerock.tictactoe.model.gamemanager

import com.triplerock.tictactoe.data.PlayerO
import com.triplerock.tictactoe.data.PlayerX
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.ui.screens.game.Crossing
import com.triplerock.tictactoe.ui.screens.game.crossingList
import com.triplerock.tictactoe.utils.Logger
import com.triplerock.tictactoe.viewmodels.GameUiState

const val invalid = -1

class GameEngine {

    fun nextMove(room: Room): Int {
        val xMoves = room.moves[PlayerX]!!
        val oMoves = room.moves[PlayerO]!!
        val availableMoves = availableMoves(xMoves, oMoves)

        // if in draw state, return invalid
        if (availableMoves.isEmpty()) return invalid

        // if first move, choose random move
        if (oMoves.isEmpty())
            return availableMoves.random()

        // check if O is about to win
        var move = winningMove(oMoves, availableMoves)
        // check if X is about to win
        if (move == invalid) move = winningMove(xMoves, availableMoves)
        // return random move from available list
        return if (move == invalid) availableMoves.random() else move
    }

    private fun winningMove(xMoves: List<Int>, availableMoves: List<Int>): Int {
        val crossMatches = arrayListOf<SinglePlayerGame.CrossMatch>()
        crossingList.forEach { crossing ->
            var matches = 0
            crossing.positions.forEach {
                if (xMoves.contains(it)) matches++
            }
            if (matches == 2)
                crossMatches.add(SinglePlayerGame.CrossMatch(crossing, matches))
        }
        crossMatches.sortByDescending { crossMatch ->
            crossMatch.matches
        }
        crossMatches.forEach { crossMatch ->
            availableMoves.forEach { move ->
                if (crossMatch.crossing.positions.contains(move)) {
                    return move
                }
            }
        }
        return invalid
    }

    private fun availableMoves(xMoves: List<Int>, oMoves: List<Int>): List<Int> {
        val exclude = arrayListOf<Int>()
        exclude.addAll(xMoves)
        exclude.addAll(oMoves)
        return (0..8).filter {
            !exclude.contains(it)
        }
    }

    fun nextState(room: Room): GameUiState {
        Logger.debug("nextTurn = $room")
        if (isDraw(room)) {
            // game draw
            room.history.draws++
            room.status = "Draw :|"
            return GameUiState.Draw(room)
        }
        var crossing = isWon(room.moves[PlayerX]!!)
        if (crossing != null) {
            // game won by playerX
            room.history.xWins++
            room.status = "$PlayerX wins!!"
            return GameUiState.Winner(
                room = room,
                crossing = crossing
            )
        }
        crossing = isWon(room.moves[PlayerO]!!)
        if (crossing != null) {
            // game won by playerO
            room.history.oWins++
            room.status = "$PlayerO wins!!"
            return GameUiState.Winner(
                room = room,
                crossing = crossing
            )
        }
        if (!room.isEmptyMoves()) {
            // change turn
            room.changeTurn()
        } else {
            room.nextTurn = PlayerX
        }
        room.status = "Turn: ${room.nextTurn}"
        Logger.info("changing turn = $room")
        return GameUiState.NextTurn(room)
    }

    private fun isWon(moves: List<Int>): Crossing? {
        for (crossing in crossingList) {
            if (moves.containsAll(crossing.positions)) {
                return crossing
            }
        }
        return null
    }

    private fun isDraw(room: Room): Boolean {
        return room.moves[PlayerX]!!.size + room.moves[PlayerO]!!.size == 9
    }
}