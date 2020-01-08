package com.andreamazzarella.amaze.core.usecases

import com.andreamazzarella.amaze.core.Game
import com.andreamazzarella.amaze.core.GameId
import com.andreamazzarella.amaze.persistence.GameNotFoundError
import com.andreamazzarella.amaze.persistence.GameRepository
import com.andreamazzarella.amaze.utils.Result

object GetAGame {
    fun doIt(gameId: GameId): Result<Game, GameNotFoundError> = GameRepository.find(gameId)
}
