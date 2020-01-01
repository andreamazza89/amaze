package com.andreamazzarella.amaze

import assertOkEquals
import com.andreamazzarella.amaze.core.DEFAULT_MAZE
import com.andreamazzarella.amaze.core.Game
import com.andreamazzarella.amaze.core.aMazeFromADrawing
import com.andreamazzarella.amaze.utils.map
import org.junit.jupiter.api.Test
import java.util.UUID

class GameTest {

    @Test
    fun `a new player is placed at the entrance of the maze`() {
        val maze = aMazeFromADrawing(DEFAULT_MAZE)
        val positions = Game(maze = maze)
            .addPlayer("runner", UUID.randomUUID())
            .map { it.playerPositions() }

        assertOkEquals(listOf(Pair("runner", maze.entrance)), positions)
    }
}
