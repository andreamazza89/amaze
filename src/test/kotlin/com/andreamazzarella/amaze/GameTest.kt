package com.andreamazzarella.amaze

import assertError
import assertOkEquals
import com.andreamazzarella.amaze.core.DEFAULT_MAZE
import com.andreamazzarella.amaze.core.Game
import com.andreamazzarella.amaze.core.aMazeFromADrawing
import com.andreamazzarella.amaze.utils.andThen
import com.andreamazzarella.amaze.utils.map
import com.andreamazzarella.amaze.utils.pipe
import org.junit.jupiter.api.Test
import java.util.UUID

class GameTest {

    @Test
    fun `a new player is placed at the entrance of the maze`() {
        val maze = aMazeFromADrawing(DEFAULT_MAZE)
        val positions = Game(maze = maze)
            .addPlayer("runner")
            .map { it.playerPositions() }

        assertOkEquals(listOf(Pair("runner", maze.entrance)), positions)
    }

    @Test
    fun `a player's name must be unique`() {
        val maze = aMazeFromADrawing(DEFAULT_MAZE)

        Game(maze = maze)
            .addPlayer("runner")
            .andThen {  it.addPlayer("runner") }
            .pipe {assertError(it) {err -> err == Game.PlayerAlreadyExists}  }

    }
}
