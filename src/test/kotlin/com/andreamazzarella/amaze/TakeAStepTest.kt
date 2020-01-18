package com.andreamazzarella.amaze

import assertIsError
import assertOk
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.Position.Column
import com.andreamazzarella.amaze.core.Position.Row
import com.andreamazzarella.amaze.core.StepDirection.DOWN
import com.andreamazzarella.amaze.core.usecases.AddAPlayer
import com.andreamazzarella.amaze.core.usecases.GetAGame
import com.andreamazzarella.amaze.core.usecases.StartAGame
import com.andreamazzarella.amaze.core.usecases.TakeAStep2
import com.andreamazzarella.amaze.core.usecases.TakeAStep2Error
import org.junit.jupiter.api.Test

class TakeAStepTest {

    @Test
    fun `a player can take a step in the maze`() {
        val gameId = StartAGame.doIt()

        AddAPlayer.doIt(gameId, "runner 1")

        TakeAStep2.doIt(gameId, "runner 1", DOWN)
        val gameUpdated = GetAGame.doIt(gameId)

        assertOk(gameUpdated) { it.playersPositions() == listOf(Pair("runner 1", Position(Row(1), Column(1)))) }
    }

    @Test
    fun `a player cannot take a step when they are not in a game`() {
        val gameId = StartAGame.doIt()

        val takeAStepResult = TakeAStep2.doIt(gameId, "runner 1", DOWN)

        assertIsError(TakeAStep2Error.PlayerNotInThisGame, takeAStepResult)
    }
}
