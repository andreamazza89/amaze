package com.andreamazzarella.amaze

import assertOk
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.StepDirection
import com.andreamazzarella.amaze.core.usecases.AddAPlayer
import com.andreamazzarella.amaze.core.usecases.PlayerStatus
import com.andreamazzarella.amaze.core.usecases.StartAGame
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PlayerStatusTest {

    @Test
    fun `find out the status of a player`() {
        val gameId = StartAGame.doIt()
        val playerName = "test-player"
        AddAPlayer.doIt(gameId, playerName)

        val statusResult = PlayerStatus.doIt(gameId, playerName)

        assertOk(statusResult) { status ->
            status as Position.Status.InsideTheMaze
            assertEquals(listOf(StepDirection.DOWN), status.directionsAvailable)
        }
    }

}