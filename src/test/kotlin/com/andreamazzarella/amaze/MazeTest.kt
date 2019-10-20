package com.andreamazzarella.amaze

import com.andreamazzarella.amaze.core.Floor
import com.andreamazzarella.amaze.core.Maze
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.Wall
import com.andreamazzarella.amaze.core.aMazeFromADrawing
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class MazeTest {

    @Test
    fun `builds a Maze from a drawing`() {
        val mazeId = UUID.randomUUID()
        val maze = aMazeFromADrawing(
            mazeId,
            """
                ⬛⚪⬛⬛⬛
                ⬛⬜⬜⬜⬤
                ⬛⬛⬛⬛⬛
            """.trimIndent()

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

    private fun pos(row: Int, column: Int) = Position(Position.Row(row), Position.Column(column))
}