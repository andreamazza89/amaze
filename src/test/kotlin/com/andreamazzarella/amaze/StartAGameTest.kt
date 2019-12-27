package com.andreamazzarella.amaze

import assertIsError
import assertIsOk
import com.andreamazzarella.amaze.core.usecases.GameDoesNotExist
import com.andreamazzarella.amaze.core.usecases.MakeAMaze
import com.andreamazzarella.amaze.core.usecases.StartAGame
import com.andreamazzarella.amaze.persistence.MazeRepository
import com.andreamazzarella.amaze.utils.pipe
import org.junit.jupiter.api.Test

class StartAGameTest {
    private val mazeRepository = MazeRepository()
    private val makeAMaze = MakeAMaze(mazeRepository)

    @Test
    fun `a maze cannot be created without a valid game`() =
        makeAMaze.doIt2("some game id that does not exist")
            .pipe { assertIsError(GameDoesNotExist, it) }

    @Test
    fun `creates a maze in a game`() =
        StartAGame.doIt()
            .pipe { makeAMaze.doIt2(it) }
            .pipe(::assertIsOk)
}
