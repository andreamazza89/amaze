package com.andreamazzarella.amaze

import assertError
import assertOk
import assertOkEquals
import com.andreamazzarella.amaze.core.DEFAULT_MAZE
import com.andreamazzarella.amaze.core.Game
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.Position.Column
import com.andreamazzarella.amaze.core.Position.Row
import com.andreamazzarella.amaze.core.StepDirection.DOWN
import com.andreamazzarella.amaze.core.aMazeFromADrawing
import com.andreamazzarella.amaze.utils.andThen
import com.andreamazzarella.amaze.utils.map
import com.andreamazzarella.amaze.utils.okOrFail
import com.andreamazzarella.amaze.utils.pipe
import org.junit.jupiter.api.Test

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
            .andThen { it.addPlayer("runner") }
            .pipe { assertError(it) { err -> err == Game.PlayerAlreadyExists } }
    }

    @Test
    fun `a player cannot take a step if they are not in the game`() {
        val maze = aMazeFromADrawing(DEFAULT_MAZE)

        val stepResult = Game(maze = maze).takeAStep("runner not in this game", DOWN)

        assertError(stepResult) { err -> err == Game.StepError.PlayerNotFound }
    }

    @Test
    fun `multiple players can take a step in a game they are in`() {
        val maze = aMazeFromADrawing(DEFAULT_MAZE)

        val initialGame = Game(maze = maze)
            .addPlayer("runner 1")
            .andThen { it.addPlayer("runner 2") }
            .okOrFail()

        val gameWithSteps = initialGame
            .takeAStep("runner 1", DOWN)
            .andThen { it.takeAStep("runner 2", DOWN) }
        val expectedNewPosition = Position(Row(1), Column(1))

        assertOk(gameWithSteps) { game ->
            game.playerPositions() == listOf(
                "runner 1" to expectedNewPosition,
                "runner 2" to expectedNewPosition
            )
        }
    }
}
