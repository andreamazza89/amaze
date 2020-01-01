package com.andreamazzarella.amaze

import assertIsError
import assertOk
import assertOkEquals
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.Position.*
import com.andreamazzarella.amaze.core.StepDirection
import com.andreamazzarella.amaze.core.StepDirection.*
import com.andreamazzarella.amaze.core.usecases.AddAPlayer
import com.andreamazzarella.amaze.core.usecases.GetAGame
import com.andreamazzarella.amaze.core.usecases.GetAMaze
import com.andreamazzarella.amaze.core.usecases.HitAWall
import com.andreamazzarella.amaze.core.usecases.MakeAMaze
import com.andreamazzarella.amaze.core.usecases.MazeNotFound
import com.andreamazzarella.amaze.core.usecases.StartAGame
import com.andreamazzarella.amaze.core.usecases.TakeAStep
import com.andreamazzarella.amaze.core.usecases.TakeAStep2
import com.andreamazzarella.amaze.core.usecases.TakeAStepError
import com.andreamazzarella.amaze.persistence.MazeRepository
import com.andreamazzarella.amaze.utils.okOrFail
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class TakeAStepTest {
    private val mazeRepository = MazeRepository()
    private val makeAMaze = MakeAMaze(mazeRepository)
    private val getAMaze = GetAMaze(mazeRepository)
    private val takeAStep = TakeAStep(mazeRepository)


    @Test
    fun `a player can take a step in the maze`() {
        val gameId = StartAGame.doIt()
        val playerId = AddAPlayer.doIt(gameId, "runner 1").okOrFail()

        TakeAStep2.doIt(gameId, playerId, DOWN)
        val gameUpdated = GetAGame.doIt(gameId)

        assertOk(gameUpdated) { it.playerPositions() == listOf(Pair("runner 1", Position(Row(1), Column(1)))) }
    }

    @Test
    fun `taking a valid step reveals the maze runner's new position`() {
        val mazeId = makeAMaze.doIt(
            """
                ⬛⚪⬛⬛
                ⬛⬜⬜⬤
                ⬛⬛⬛⬛
            """.trimIndent()
        )

        val stepResult = takeAStep.doIt(mazeId, DOWN)

        assertOkEquals(Position(Row(1), Column(1)), stepResult)
    }

    @Test
    fun `the runner's new position is updated in the Maze after a valid step`() {
        val mazeId = makeAMaze.doIt(
            """
                ⬛⚪⬛⬛
                ⬛⬜⬜⬤
                ⬛⬛⬛⬛
            """.trimIndent()
        )

        takeAStep.doIt(mazeId, DOWN)

        val updatedMaze = getAMaze.doIt(mazeId)
        val expectedPosition = Position(Row(1), Column(1))
        assertEquals(expectedPosition, updatedMaze.okOrFail().currentPosition)
    }

    @Test
    fun `trying to take a step with an invalid mazeId results in an error`() {
        val stepResult = takeAStep.doIt(UUID.randomUUID(), UP)

        assertIsError(TakeAStepError(MazeNotFound), stepResult)
    }

    @Test
    fun `walking into a wall results in an error`() {
        val mazeId = makeAMaze.doIt(
            """
                ⬛⬜⬛⬛
                ⬛⚪⬜⬤
                ⬛⬛⬛⬛
            """.trimIndent()
        )

        val stepResult = takeAStep.doIt(mazeId, LEFT)

        assertIsError(TakeAStepError(HitAWall), stepResult)
    }

    @Test
    fun `the runner's position stays the same after an invalid step`() {
        val mazeId = makeAMaze.doIt(
            """
                ⬛⚪⬛⬛
                ⬛⬜⬜⬤
                ⬛⬛⬛⬛
            """.trimIndent()
        )

        takeAStep.doIt(mazeId, RIGHT)

        val updatedMaze = getAMaze.doIt(mazeId)
        val expectedPosition = Position(Row(0), Column(1))
        assertEquals(expectedPosition, updatedMaze.okOrFail().currentPosition)
    }
}

