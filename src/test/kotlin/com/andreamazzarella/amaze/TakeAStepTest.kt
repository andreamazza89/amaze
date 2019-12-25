package com.andreamazzarella.amaze

import com.andreamazzarella.amaze.core.usecases.GetAMaze
import com.andreamazzarella.amaze.core.usecases.HitAWall
import com.andreamazzarella.amaze.core.usecases.MakeAMaze
import com.andreamazzarella.amaze.core.usecases.MazeNotFound
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.StepDirection
import com.andreamazzarella.amaze.core.usecases.TakeAStepError
import com.andreamazzarella.amaze.core.usecases.TakeAStep
import com.andreamazzarella.amaze.persistence.MazeRepository
import com.andreamazzarella.amaze.utils.Err
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.Result
import com.andreamazzarella.amaze.utils.okOrFail
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import java.util.UUID

class TakeAStepTest {
    private val mazeRepository = MazeRepository()
    private val makeAMaze = MakeAMaze(mazeRepository)
    private val getAMaze = GetAMaze(mazeRepository)
    private val takeAStep = TakeAStep(mazeRepository)

    @Test
    fun `taking a valid step reveals the maze runner's new position`() {
        val mazeId = makeAMaze.doIt(
            """
                ⬛⚪⬛⬛
                ⬛⬜⬜⬤
                ⬛⬛⬛⬛
            """.trimIndent()
        )

        val stepResult = takeAStep.doIt(mazeId, StepDirection.DOWN)

        assertOkEquals(Position(Position.Row(1), Position.Column(1)), stepResult)
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

        takeAStep.doIt(mazeId, StepDirection.DOWN)

        val updatedMaze = getAMaze.doIt(mazeId)
        val expectedPosition = Position(Position.Row(1), Position.Column(1))
        assertEquals(expectedPosition, updatedMaze.okOrFail().currentPosition)
    }

    @Test
    fun `trying to take a step with an invalid mazeId results in an error`() {
        val stepResult = takeAStep.doIt(UUID.randomUUID(), StepDirection.UP)

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

        val stepResult = takeAStep.doIt(mazeId, StepDirection.LEFT)

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

        takeAStep.doIt(mazeId, StepDirection.RIGHT)

        val updatedMaze = getAMaze.doIt(mazeId)
        val expectedPosition = Position(Position.Row(0), Position.Column(1))
        assertEquals(expectedPosition, updatedMaze.okOrFail().currentPosition)
    }

}

private fun <O, E> assertOkEquals(expected: O, result: Result<O, E>) {
    when (result) {
        is Ok -> assertEquals(result.okValue, expected)
        is Err -> fail("Expected result to be ok, but was an error")
    }
}

private fun <O, E> assertIsError(expected: E, result: Result<O, E>) {
    when (result) {
        is Ok -> fail("Expected result to be an error, but was ok")
        is Err -> assertEquals(expected, result.errorValue)
    }
}
