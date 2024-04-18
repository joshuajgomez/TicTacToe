package com.triplerock.tictactoe.model.gamemanager

import com.triplerock.tictactoe.data.PlayerO
import com.triplerock.tictactoe.data.PlayerX
import com.triplerock.tictactoe.data.Room
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class SinglePlayerGameTest {

    private lateinit var game: GameManager

    @MockK
    private lateinit var callback: GameManager.Callback

    private lateinit var room: Room

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        room = Room()

        game = SinglePlayerGame()
        game.listenUpdates("", callback)

        justRun { callback.onRoomUpdate(room) }
    }

    @After
    fun tearDown() {
        printMoves()
    }

    private fun printMoves() {
        val gameMap = hashMapOf<Int, Char>()
        repeat(9) {
            gameMap[it] = '-'
        }
        room.moves[PlayerX]!!.forEach {
            gameMap[it] = 'X'
        }
        room.moves[PlayerO]!!.forEach {
            gameMap[it] = 'O'
        }
        gameMap.forEach { (cell, mark) ->
            print("\t$mark")
            if ((cell + 1) % 3 == 0) println()
        }
        println()
    }

    @Test
    fun `onMove PlayerO empty, PlayerX empty`() {
        printMoves()

        // method call
        game.onMove(room)

        // verify
        verify { callback.onRoomUpdate(room) }
        assert(room.moves[PlayerO]!!.isNotEmpty())
    }

    @Test
    fun `onMove PlayerO empty, PlayerX notEmpty`() {
        // all cells filled except 4
        room.moves[PlayerX] = arrayListOf(0, 1, 2, 3, 5, 6, 7, 8)
        printMoves()

        // method call
        game.onMove(room)

        // verify O moves to 4
        verify { callback.onRoomUpdate(room) }
        assert(room.moves[PlayerO]?.first() == 4)
    }

    @Test
    fun `onMove PlayerO notEmpty, PlayerX empty`() {
        room.moves[PlayerO] = arrayListOf(0, 4)
        printMoves()

        // method call
        game.onMove(room)

        // verify O's winning move 8
        verify { callback.onRoomUpdate(room) }
        assert(room.moves[PlayerO]!!.contains(8))
    }

    @Test
    fun `onMove PlayerO notEmpty, PlayerX notEmpty`() {
        room.moves[PlayerO] = arrayListOf(0, 4, 8)
        room.moves[PlayerX] = arrayListOf(1, 2)
        printMoves()

        // method call
        game.onMove(room)

        // verify O's winning move 8
        verify { callback.onRoomUpdate(room) }
        assert(room.moves[PlayerO]!!.size == 4)
    }

    @Test
    fun `onMove PlayerO notEmpty, PlayerX notEmpty2`() {
        room.moves[PlayerO] = arrayListOf(1, 2)
        room.moves[PlayerX] = arrayListOf(3, 8, 7, 4)
        printMoves()

        // method call
        game.onMove(room)

        // verify O's winning move 8
        verify { callback.onRoomUpdate(room) }
        assert(room.moves[PlayerO]!!.contains(0))
    }
}