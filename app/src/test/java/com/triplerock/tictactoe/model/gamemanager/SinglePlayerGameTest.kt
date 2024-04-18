package com.triplerock.tictactoe.model.gamemanager

import com.triplerock.tictactoe.data.PlayerO
import com.triplerock.tictactoe.data.Room
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.verify
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

    @Test
    fun test_onMove_EmptyMoves() {
        // pre-condition
        assert(room.moves[PlayerO]!!.isEmpty())

        // method call
        game.onMove(room)

        // verify
        verify { callback.onRoomUpdate(room) }
        assert(room.moves[PlayerO]!!.isNotEmpty())
    }
}