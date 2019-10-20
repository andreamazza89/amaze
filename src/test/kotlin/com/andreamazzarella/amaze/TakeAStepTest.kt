package com.andreamazzarella.amaze

import com.andreamazzarella.amaze.core.GetAMaze
import com.andreamazzarella.amaze.core.MakeAMaze
import com.andreamazzarella.amaze.core.MazeNotFound
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.StepDirection
import com.andreamazzarella.amaze.core.StepError
import com.andreamazzarella.amaze.core.TakeAStep
import com.andreamazzarella.amaze.persistence.MazeRepository
import com.andreamazzarella.amaze.utils.Err
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.Result
import com.andreamazzarella.amaze.utils.okOrFail
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class TakeAStepTest {
    private val mazeRepository = MazeRepository()
    private val makeAMaze = MakeAMaze(mazeRepository)
    private val getAMaze = GetAMaze(mazeRepository)
    private val takeAStep = TakeAStep(mazeRepository)

    @BeforeEach
    fun initialise() {
        mazeRepository.deleteAll()
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

        val stepResult = takeAStep.doIt(mazeId, StepDirection.DOWN)

        assertOkEquals(Position(Position.Row(1), Position.Column(1)), stepResult)
    }

    @Test
    fun `the runner's new position is updated in the Maze after a valid move`() {
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

        assertIsError(StepError(MazeNotFound), stepResult)
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
