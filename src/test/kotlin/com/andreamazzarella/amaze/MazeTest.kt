package com.andreamazzarella.amaze

import assertIsError
import assertOkEquals
import com.andreamazzarella.amaze.core.Cell.Floor
import com.andreamazzarella.amaze.core.Cell.Wall
import com.andreamazzarella.amaze.core.Maze
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.StepDirection.DOWN
import com.andreamazzarella.amaze.core.StepDirection.LEFT
import com.andreamazzarella.amaze.core.StepDirection.RIGHT
import com.andreamazzarella.amaze.core.StepDirection.UP
import com.andreamazzarella.amaze.core.StepError
import com.andreamazzarella.amaze.core.aMazeFromADrawing
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
            id = mazeId
        )

        val expectedMaze = Maze(
            id = mazeId,
            entrance = pos(0, 1),
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
    fun `knows the status of a position (example one - DOWN available)`() {
        val maze = aMazeFromADrawing(
            """
                ⬛⚪⬛⬛⬛
                ⬛⬜⬜⬜⬤
                ⬛⬛⬛⬛⬛
            """.trimIndent()
        )

        val status = maze.positionStatus(pos(0, 1))

        assertEquals(listOf(DOWN), (status as Position.Status.InsideTheMaze).directionsAvailable)
    }

    @Test
    fun `knows the status of a position (example two - DOWN RIGHT LEFT available)`() {
        val maze = aMazeFromADrawing(
            """
                ⬛⬜⬛⬛⬛
                ⬛⬜⚪⬜⬤
                ⬛⬛⬜⬛⬛
                ⬛⬛⬛⬛⬛
            """.trimIndent()
        )

        val status = maze.positionStatus(pos(1, 2))

        assertEquals(listOf(RIGHT, DOWN, LEFT), (status as Position.Status.InsideTheMaze).directionsAvailable)
    }

    @Test
    fun `knows the status of a position (example three - got to the exit)`() {
        val maze = aMazeFromADrawing(
            """
                ⬛⬜⬛⬛⬛
                ⬛⬜⬜⬜⬤
                ⬛⬛⬛⬛⬛
            """.trimIndent()
        )

        val status = maze.positionStatus(pos(1, 4))

        assertEquals(Position.Status.OutsideTheMaze, status)
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

        val stepResult = maze.takeAStep(pos(1, 1), UP)


        assertOkEquals(pos(0, 1), stepResult)
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

        val stepResult = maze.takeAStep(pos(0, 1), DOWN)


        assertOkEquals(pos(1, 1), stepResult)
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

        val stepResult = maze.takeAStep(pos(1, 1), RIGHT)


        assertOkEquals(pos(1, 2), stepResult)
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

        val stepResult = maze.takeAStep(pos(1, 2), LEFT)


        assertOkEquals(pos(1, 1), stepResult)
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

        val stepResult = maze.takeAStep(pos(1, 2), DOWN)

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

        val stepResult = maze.takeAStep(pos(0, 1), UP)

        assertIsError(StepError("you walked out of the maze"), stepResult)
    }

    private fun pos(row: Int, column: Int) = Position(Position.Row(row), Position.Column(column))
}
