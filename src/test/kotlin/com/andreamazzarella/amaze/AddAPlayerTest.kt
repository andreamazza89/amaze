package com.andreamazzarella.amaze

import assertIsError
import assertIsOk
import com.andreamazzarella.amaze.core.usecases.AddAPlayer
import com.andreamazzarella.amaze.core.usecases.AddAPlayerError
import com.andreamazzarella.amaze.core.usecases.StartAGame
import com.andreamazzarella.amaze.utils.pipe
import org.junit.jupiter.api.Test

class AddAPlayerTest {

    @Test
    fun `adds a player to a game`() =
        StartAGame.doIt()
            .pipe { AddAPlayer.doIt(it, "runner 1") }
            .pipe(::assertIsOk)

    @Test
    fun `a player cannot be added without a valid game id`() =
        AddAPlayer.doIt("some game id that does not exist", "runner 1")
            .pipe { assertIsError(AddAPlayerError.GameDoesNotExist, it) }

    @Test
    fun `a player cannot be added if another player with the same name already exists`() {
        val gameId = StartAGame.doIt()

        AddAPlayer.doIt(gameId, "fave runner")

        AddAPlayer.doIt(gameId, "fave runner")
            .pipe { assertIsError(AddAPlayerError.PlayerAlreadyExists, it) }

    }
}
