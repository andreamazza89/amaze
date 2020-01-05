package com.andreamazzarella.amaze.persistence

import com.andreamazzarella.amaze.core.Game
import com.andreamazzarella.amaze.core.GameId
import com.andreamazzarella.amaze.core.MazeId
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

    fun updateGame(game: Game): Result<Unit, GameNotFoundError> {
        games[game.id] = game
        return Ok(Unit)
    }

    fun findGameWithMaze(mazeId: MazeId): Result<Game, MazeNotFoundError> {
        return Ok(games.values.find { it.mazes.find { it.id == mazeId } != null }!!)
    }
}

object GameNotFoundError
