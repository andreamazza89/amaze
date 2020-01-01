package com.andreamazzarella.amaze.core

import com.andreamazzarella.amaze.core.Position.*
import com.andreamazzarella.amaze.utils.Ok
import java.util.UUID
import com.andreamazzarella.amaze.utils.Result

data class Game(
    val id: GameId = generateGameId(),
    private val maze: Maze = aMazeFromADrawing(DEFAULT_MAZE),
    val mazes: List<Maze> = emptyList(),
    private val players: List<Player> = emptyList()
) {

    fun withMaze(maze: Maze) = this.copy(mazes = mazes + maze)

    fun updateMaze(maze: Maze) = this.copy(mazes = mazes.filter { it.id != maze.id } + maze)
    fun addPlayer(playerName: String, playerId: PlayerId): Result<Game, PlayerAlreadyExists> {
        return Ok(
            this.copy(players = this.players + Player(playerName, playerId, maze.entrance))
        )
    }

    fun playerPositions(): List<Pair<String, Position>> {
        return this.players.map { it.name to it.currentPosition }
    }

    object PlayerAlreadyExists
}

data class Player(val name: String, val id: PlayerId, val currentPosition: Position)

typealias GameId = String
typealias PlayerId = UUID

fun generateGameId(): String =
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
