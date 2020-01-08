package com.andreamazzarella.amaze.core.usecases

import com.andreamazzarella.amaze.core.Game
import com.andreamazzarella.amaze.core.GameId
import com.andreamazzarella.amaze.core.StepDirection
import com.andreamazzarella.amaze.persistence.GameRepository
import com.andreamazzarella.amaze.utils.Result
import com.andreamazzarella.amaze.utils.andThen
import com.andreamazzarella.amaze.utils.map
import com.andreamazzarella.amaze.utils.mapError

object TakeAStep2 {
    fun doIt(gameId: GameId, playerName: String, direction: StepDirection): Result<GameId, TakeAStep2Error> =
        findGame(gameId)
            .andThen { takeAStep(it, playerName, direction) }
            .map { GameRepository.save(it) }

    private fun findGame(gameId: GameId): Result<Game, TakeAStep2Error> =
        GameRepository.find(gameId)
            .mapError { TakeAStep2Error.GameDoesNotExist }

    private fun takeAStep(game: Game, playerName: String, direction: StepDirection): Result<Game, TakeAStep2Error> =
        game.takeAStep(playerName, direction)
            .mapError { TakeAStep2Error.InvalidStep }
}

sealed class TakeAStep2Error {
    object GameDoesNotExist : TakeAStep2Error()
    object InvalidStep : TakeAStep2Error()
}
