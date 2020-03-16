package com.andreamazzarella.amaze

import assertOk
import com.andreamazzarella.amaze.core.StepDirection
import com.andreamazzarella.amaze.core.usecases.AddAPlayer
import com.andreamazzarella.amaze.core.usecases.DirectionsAvailable
import com.andreamazzarella.amaze.core.usecases.StartAGame
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DirectionsAvailableTest {

    @Test
    fun `find out the directions available to a player`() {
        val gameId = StartAGame.doIt()
        val playerName = "test-player"
        AddAPlayer.doIt(gameId, playerName)

        val directionsResult = DirectionsAvailable.doIt(gameId, playerName)

        assertOk(directionsResult) {
            assertEquals(listOf(StepDirection.DOWN), it)
        }
    }

}