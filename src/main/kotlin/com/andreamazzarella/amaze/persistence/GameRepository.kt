package com.andreamazzarella.amaze.persistence

import com.andreamazzarella.amaze.core.Game
import com.andreamazzarella.amaze.core.GameId
import com.andreamazzarella.amaze.utils.Err
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.pipe
import com.andreamazzarella.amaze.utils.Result

object GameRepository {
    private val games: MutableMap<GameId, Game> = mutableMapOf()

    fun save(): GameId =
        generateGameId()
            .pipe {persist(it)}


    private fun persist(gameId: GameId): GameId {
        games[gameId] = Game()
        return gameId
    }

    fun find(gameId: GameId): Result<Game, GameNotFoundError> {
        return if (games[gameId] != null) {
            Ok(games[gameId]!!)
        } else {
            Err(GameNotFoundError)
        }
    }
}

object GameNotFoundError

private fun generateGameId(): String =
    listOf(
        RandomCharacter.NUMBER,
        RandomCharacter.NUMBER,
        RandomCharacter.NUMBER,
        RandomCharacter.LOWER,
        RandomCharacter.LOWER,
        RandomCharacter.LOWER,
        RandomCharacter.LOWER
    ).shuffled()
        .map { it.randomMember() }
        .joinToString(separator = "")

private enum class RandomCharacter(val values: CharRange) {
    LOWER('a'..'z'), NUMBER('0'..'9');

    fun randomMember(): Char {
        return values.random()
    }
}
