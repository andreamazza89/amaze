package com.andreamazzarella.amaze.core.usecases

import com.andreamazzarella.amaze.core.Game
import com.andreamazzarella.amaze.core.StepDirection
import com.andreamazzarella.amaze.persistence.GameRepository
import com.andreamazzarella.amaze.utils.Result
import com.andreamazzarella.amaze.utils.andThen
import com.andreamazzarella.amaze.utils.mapError
import com.andreamazzarella.amaze.web.GameId

object DirectionsAvailable {
    fun doIt(gameId: GameId, playerName: String): Result<List<StepDirection>, DirectionsAvailableError> =
        findGame(gameId)
            .andThen { directionsFor(playerName, it) }

    private fun findGame(gameId: GameId): Result<Game, DirectionsAvailableError> =
        GameRepository.find(gameId)
            .mapError { DirectionsAvailableError }

    private fun directionsFor(playerName: String, game: Game): Result<List<StepDirection>, DirectionsAvailableError> =
        game.directionsAvailableFor(playerName)
            .mapError { DirectionsAvailableError }
}

object DirectionsAvailableError