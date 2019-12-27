package com.andreamazzarella.amaze

import assertIsError
import assertIsOk
import assertOk
import assertOkEquals
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.Position.*
import com.andreamazzarella.amaze.core.StepDirection.DOWN
import com.andreamazzarella.amaze.core.usecases.GameDoesNotExist
import com.andreamazzarella.amaze.core.usecases.GetAMaze
import com.andreamazzarella.amaze.core.usecases.MakeAMaze
import com.andreamazzarella.amaze.core.usecases.StartAGame
import com.andreamazzarella.amaze.core.usecases.TakeAStep
import com.andreamazzarella.amaze.persistence.MazeRepository
import com.andreamazzarella.amaze.utils.okOrFail
import com.andreamazzarella.amaze.utils.pipe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StartAGameTest {
    private val mazeRepository = MazeRepository()
    private val makeAMaze = MakeAMaze(mazeRepository)
    private val takeAStep = TakeAStep(mazeRepository)
    private val getAMaze = GetAMaze(mazeRepository)

    @Test
    fun `a maze cannot be created without a valid game id`() =
        makeAMaze.doIt2("some game id that does not exist")
            .pipe { assertIsError(GameDoesNotExist, it) }

    @Test
    fun `creates a maze in a game`() =
        StartAGame.doIt()
            .pipe { makeAMaze.doIt2(it) }
            .pipe(::assertIsOk)

    @Test
    fun `takes a step in a maze`() {
        val mazeDrawing =
            """
                ⬛⚪⬛⬛
                ⬛⬜⬜⬤
                ⬛⬛⬛⬛
            """.trimIndent()

        val mazeId = StartAGame.doIt()
            .pipe { gameId -> makeAMaze.doIt2(gameId, mazeDrawing) }
            .okOrFail()

        takeAStep.doIt2(mazeId, DOWN)
        val mazeUpdated = getAMaze.doIt2(mazeId)

        assertOk(mazeUpdated) { maze -> assertEquals(Position(Row(1), Column(1)), maze.currentPosition)}
    }
}
