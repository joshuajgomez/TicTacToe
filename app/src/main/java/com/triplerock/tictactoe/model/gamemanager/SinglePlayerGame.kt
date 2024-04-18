package com.triplerock.tictactoe.model.gamemanager

import com.triplerock.tictactoe.data.PlayerO
import com.triplerock.tictactoe.data.PlayerX
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.ui.screens.game.Crossing
import com.triplerock.tictactoe.ui.screens.game.crossingList

const val invalid = -1

class SinglePlayerGame : GameManager {

    private lateinit var callback: GameManager.Callback

    override fun listenUpdates(roomId: String, callback: GameManager.Callback) {
        this.callback = callback
    }

    data class CrossMatch(
        val crossing: Crossing,
        val matches: Int,
    ) {
        override fun toString(): String {
            return "CrossMatch(crossing=${crossing.positions}, matches=$matches)"
        }
    }

    override fun onMove(room: Room) {
        val xMoves = room.moves[PlayerX]!!
        val oMoves = room.moves[PlayerO]!!
        val availableMoves = availableMoves(xMoves, oMoves)

        val nextMove: Int = if (oMoves.isEmpty()) {
            availableMoves.random()
        } else {
            nextMove(xMoves, oMoves, availableMoves)
        }
        println("nextMove = $nextMove")
        oMoves.add(nextMove)
        callback.onRoomUpdate(room)
    }

    private fun nextMove(
        xMoves: List<Int>,
        oMoves: List<Int>,
        availableMoves: List<Int>,
    ): Int {
        // check if X is about to win
        var move = winningMove(xMoves, availableMoves)
        if (move == invalid) move = winningMove(oMoves, availableMoves)
        return if (move == invalid) availableMoves.random() else move
    }

    private fun winningMove(xMoves: List<Int>, availableMoves: List<Int>): Int {
        val crossMatches = arrayListOf<CrossMatch>()
        crossingList.forEach { crossing ->
            var matches = 0
            crossing.positions.forEach {
                if (xMoves.contains(it)) matches++
            }
            if (matches == 2)
                crossMatches.add(CrossMatch(crossing, matches))
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

    override fun clearMoves(room: Room) {
        TODO("Not yet implemented")
    }
}