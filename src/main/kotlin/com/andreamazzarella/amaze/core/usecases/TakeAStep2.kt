package com.andreamazzarella.amaze.core.usecases

import com.andreamazzarella.amaze.core.Game
import com.andreamazzarella.amaze.core.GameId
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.StepDirection
import com.andreamazzarella.amaze.persistence.GameRepository
import com.andreamazzarella.amaze.utils.Result
import com.andreamazzarella.amaze.utils.andThen
import com.andreamazzarella.amaze.utils.mapError
import com.andreamazzarella.amaze.utils.runOnOk

object TakeAStep2 {
    fun doIt(gameId: GameId, playerName: String, direction: StepDirection): Result<Position, TakeAStep2Error> =
        findGame(gameId)
            .andThen { takeAStep(it, playerName, direction) }
            .runOnOk { GameRepository.save(it) }
            .andThen { game -> playerPosition(game, playerName) }

    private fun findGame(gameId: GameId): Result<Game, TakeAStep2Error> =
        GameRepository.find(gameId)
            .mapError { TakeAStep2Error.GameDoesNotExist }

    private fun takeAStep(game: Game, playerName: String, direction: StepDirection): Result<Game, TakeAStep2Error> =
        game.takeAStep(playerName, direction)
            .mapError { err ->
                when (err) {
                    Game.StepError.PlayerNotFound -> TakeAStep2Error.PlayerNotInThisGame
                    Game.StepError.InvalidStep -> TakeAStep2Error.InvalidStep
                }
            }

    private fun playerPosition(game: Game, playerName: String): Result<Position, TakeAStep2Error> =
        game.playerPosition(playerName)
            .mapError { TakeAStep2Error.PlayerNotInThisGame }

}

sealed class TakeAStep2Error {
    object GameDoesNotExist : TakeAStep2Error()
    object InvalidStep : TakeAStep2Error()
    object PlayerNotInThisGame : TakeAStep2Error()
}
