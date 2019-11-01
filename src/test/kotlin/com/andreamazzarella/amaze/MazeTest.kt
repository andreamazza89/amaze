package com.andreamazzarella.amaze

import com.andreamazzarella.amaze.core.Floor
import com.andreamazzarella.amaze.core.StepError
import com.andreamazzarella.amaze.core.Maze
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.StepDirection
import com.andreamazzarella.amaze.core.Wall
import com.andreamazzarella.amaze.core.aMazeFromADrawing
import com.andreamazzarella.amaze.utils.Err
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.Result
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class MazeTest {

    @Test
    fun `builds a Maze from a drawing`() {
        val mazeId = UUID.randomUUID()
        val maze = aMazeFromADrawing(
            """
                ⬛⚪⬛⬛⬛
                ⬛⬜⬜⬜⬤
                ⬛⬛⬛⬛⬛
            """.trimIndent(),
            mazeId
        )

        val expectedMaze = Maze(
            id = mazeId,
            currentPosition = pos(0, 1),
            exit = pos(1, 4),
            cells = listOf(
                Wall(pos(0, 0)), Floor(pos(0, 1)), Wall(pos(0, 2)), Wall(pos(0, 3)), Wall(pos(0, 4)),
                Wall(pos(1, 0)), Floor(pos(1, 1)), Floor(pos(1, 2)), Floor(pos(1, 3)), Floor(pos(1, 4)),
                Wall(pos(2, 0)), Wall(pos(2, 1)), Wall(pos(2, 2)), Wall(pos(2, 3)), Wall(pos(2, 4))
            )
        )

        assertEquals(expectedMaze, maze)
    }

    @Test
    fun `takes a step up in the maze`() {
        val maze = aMazeFromADrawing(
            """
                ⬛⬜⬛⬛⬛
                ⬛⚪⬜⬜⬤
                ⬛⬛⬛⬛⬛
            """.trimIndent()
        )

        val stepResult = maze.takeAStep(StepDirection.UP)


        assertOkEquals(pos(0,1), stepResult)
    }

    @Test
    fun `takes a step down in the maze`() {
        val maze = aMazeFromADrawing(
            """
                ⬛⚪⬛⬛⬛
                ⬛⬜⬜⬜⬤
                ⬛⬛⬛⬛⬛
            """.trimIndent()
        )

        val stepResult = maze.takeAStep(StepDirection.DOWN)


        assertOkEquals(pos(1,1), stepResult)
    }

    @Test
    fun `takes a step right in the maze`() {
        val maze = aMazeFromADrawing(
            """
                ⬛⬜⬛⬛⬛
                ⬛⚪⬜⬜⬤
                ⬛⬛⬛⬛⬛
            """.trimIndent()
        )

        val stepResult = maze.takeAStep(StepDirection.RIGHT)


        assertOkEquals(pos(1,2), stepResult)
    }

    @Test
    fun `takes a step left in the maze`() {
        val maze = aMazeFromADrawing(
            """
                ⬛⬜⬛⬛⬛
                ⬛⬜⚪⬜⬤
                ⬛⬛⬛⬛⬛
            """.trimIndent()
        )

        val stepResult = maze.takeAStep(StepDirection.LEFT)


        assertOkEquals(pos(1,1), stepResult)
    }

    @Test
    fun `hit a wall going down`() {
        val maze = aMazeFromADrawing(
            """
                ⬛⬜⬛⬛⬛
                ⬛⬜⚪⬜⬤
                ⬛⬛⬛⬛⬛
            """.trimIndent()
        )

        val stepResult = maze.takeAStep(StepDirection.DOWN)

        assertIsError(StepError(), stepResult)
    }

    @Test
    fun `walking out of the maze is not allowed`() {
        val maze = aMazeFromADrawing(
            """
                ⬛⚪⬛⬛⬛
                ⬛⬜⬜⬜⬤
                ⬛⬛⬛⬛⬛
            """.trimIndent()
        )

        val stepResult = maze.takeAStep(StepDirection.UP)

        assertIsError(StepError("you walked out of the maze"), stepResult)
    }



    private fun pos(row: Int, column: Int) = Position(Position.Row(row), Position.Column(column))

    private fun <O, E> assertOkEquals(expected: O, result: Result<O, E>) {
        when (result) {
            is Ok -> assertEquals(result.okValue, expected)
            is Err -> Assertions.fail("Expected result to be ok, but was an error")
        } }

    private fun <O, E> assertIsError(expected: E, result: Result<O, E>) {
        when (result) {
            is Ok -> Assertions.fail("Expected result to be an error, but was ok")
            is Err -> assertEquals(expected, result.errorValue)
        }
    }
}