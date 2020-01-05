package com.andreamazzarella.amaze.core.usecases

import com.andreamazzarella.amaze.core.Game
import com.andreamazzarella.amaze.core.GameId
import com.andreamazzarella.amaze.persistence.GameRepository
import com.andreamazzarella.amaze.utils.Result
import com.andreamazzarella.amaze.utils.andThen
import com.andreamazzarella.amaze.utils.map
import com.andreamazzarella.amaze.utils.mapError

object AddAPlayer {
    fun doIt(gameId: GameId, playerName: String): Result<String, AddAPlayerError> =
        findGame(gameId)
            .andThen { addPlayer(it, playerName) }
            .map { GameRepository.save(it) }
            .map { playerName }

    private fun findGame(gameId: GameId): Result<Game, AddAPlayerError> =
        GameRepository.find(gameId)
            .mapError { AddAPlayerError.GameDoesNotExist }

    private fun addPlayer(game: Game, playerName: String): Result<Game, AddAPlayerError> =
        game.addPlayer(playerName)
            .mapError { AddAPlayerError.PlayerAlreadyExists }
}

sealed class AddAPlayerError {
    object GameDoesNotExist : AddAPlayerError()
    object PlayerAlreadyExists : AddAPlayerError()
}
