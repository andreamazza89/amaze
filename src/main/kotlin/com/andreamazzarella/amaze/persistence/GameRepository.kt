package com.andreamazzarella.amaze.persistence

import com.andreamazzarella.amaze.core.Game
import com.andreamazzarella.amaze.core.GameId
import com.andreamazzarella.amaze.utils.Err
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.Result

object GameRepository {
    private val games: MutableMap<GameId, Game> = mutableMapOf()

    fun save(game: Game): GameId {
        this.games[game.id] = game
        return game.id
    }

    fun find(gameId: GameId): Result<Game, GameNotFoundError> {
        return if (games[gameId] != null) {
            Ok(games[gameId]!!)
        } else {
            Err(GameNotFoundError)
        }
    }

    fun findAll(): List<Game> =
        games.values.toList()
}

object GameNotFoundError
