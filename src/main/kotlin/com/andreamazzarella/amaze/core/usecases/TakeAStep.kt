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

object TakeAStep {
    fun doIt(gameId: GameId, playerName: String, direction: StepDirection): Result<Position, TakeAStepError> =
        findGame(gameId)
            .andThen { takeAStep(it, playerName, direction) }
            .runOnOk { GameRepository.save(it) }
            .andThen { game -> playerPosition(game, playerName) }

    private fun findGame(gameId: GameId): Result<Game, TakeAStepError> =
        GameRepository.find(gameId)
            .mapError { TakeAStepError.GameDoesNotExist }

    private fun takeAStep(game: Game, playerName: String, direction: StepDirection): Result<Game, TakeAStepError> =
        game.takeAStep(playerName, direction)
            .mapError { err ->
                when (err) {
                    Game.StepError.PlayerNotFound -> TakeAStepError.PlayerNotInThisGame
                    Game.StepError.InvalidStep -> TakeAStepError.InvalidStep
                }
            }

    private fun playerPosition(game: Game, playerName: String): Result<Position, TakeAStepError> =
        game.playerPosition(playerName)
            .mapError { TakeAStepError.PlayerNotInThisGame }

}

sealed class TakeAStepError {
    object GameDoesNotExist : TakeAStepError()
    object InvalidStep : TakeAStepError()
    object PlayerNotInThisGame : TakeAStepError()
}
