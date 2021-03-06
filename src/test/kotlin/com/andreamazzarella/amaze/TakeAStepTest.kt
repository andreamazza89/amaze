package com.andreamazzarella.amaze

import assertIsError
import assertOk
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.Position.Column
import com.andreamazzarella.amaze.core.Position.Row
import com.andreamazzarella.amaze.core.StepDirection.DOWN
import com.andreamazzarella.amaze.core.StepDirection.LEFT
import com.andreamazzarella.amaze.core.StepDirection.RIGHT
import com.andreamazzarella.amaze.core.aMazeFromADrawing
import com.andreamazzarella.amaze.core.usecases.AddAPlayer
import com.andreamazzarella.amaze.core.usecases.GetAGame
import com.andreamazzarella.amaze.core.usecases.StartAGame
import com.andreamazzarella.amaze.core.usecases.TakeAStep
import com.andreamazzarella.amaze.core.usecases.TakeAStepError
import org.junit.jupiter.api.Test

class TakeAStepTest {

    @Test
    fun `a player can take a step in the maze`() {
        val gameId = StartAGame.doIt()

        AddAPlayer.doIt(gameId, "runner 1")

        TakeAStep.doIt(gameId, "runner 1", DOWN, true)
        val gameUpdated = GetAGame.doIt(gameId)

        assertOk(gameUpdated) { it.playersPositions() == listOf(Pair("runner 1", Position(Row(1), Column(1)))) }
    }

    @Test
    fun `a player cannot take a step when they are not in a game`() {
        val gameId = StartAGame.doIt()

        val takeAStepResult = TakeAStep.doIt(gameId, "runner 1", DOWN, true)

        assertIsError(TakeAStepError.PlayerNotInThisGame, takeAStepResult)
    }

    @Test
    fun `a player cannot take a step when they have an invalid token`() {
        val gameId = StartAGame.doIt()

        AddAPlayer.doIt(gameId, "runner 1")

        val takeAStepResult = TakeAStep.doIt(gameId, "runner 1", DOWN, false)

        assertIsError(TakeAStepError.TokenIsNotValid, takeAStepResult)
    }

    @Test
    fun `a player cannot take a step when they have got out`() {
        val gameId = StartAGame.doIt(
            aMazeFromADrawing(
                """
                ⬛⚪⬛
                ⬛⬜⬤
                ⬛⬛⬛
            """.trimIndent()
            )
        )

        AddAPlayer.doIt(gameId, "runner 1")

        TakeAStep.doIt(gameId, "runner 1", DOWN, true)
        TakeAStep.doIt(gameId, "runner 1", RIGHT, true)

        val takeAStepResult = TakeAStep.doIt(gameId, "runner 1", LEFT, true)

        assertIsError(TakeAStepError.PlayerGotOut, takeAStepResult)
    }

}
