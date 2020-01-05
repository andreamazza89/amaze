package com.andreamazzarella.amaze.core

import com.andreamazzarella.amaze.utils.Err
import com.andreamazzarella.amaze.utils.Ok
import java.util.UUID
import com.andreamazzarella.amaze.utils.Result
import com.andreamazzarella.amaze.utils.map

data class Game(
    val id: GameId = generateGameId(),
    private val maze: Maze = aMazeFromADrawing(DEFAULT_MAZE),
    val mazes: List<Maze> = emptyList(),
    private val players: List<Player> = emptyList()
) {

    fun withMaze(maze: Maze) = this.copy(mazes = mazes + maze)

    fun updateMaze(maze: Maze) = this.copy(mazes = mazes.filter { it.id != maze.id } + maze)

    fun addPlayer(playerName: String): Result<Game, PlayerAlreadyExists> =
        checkPlayerCanBeAdded(playerName)
            .map { this.copy(players = this.players + Player(playerName, maze.entrance)) }

    fun playerPositions(): List<Pair<String, Position>> {
        return this.players.map { it.name to it.currentPosition }
    }

    private fun checkPlayerCanBeAdded(playerName: String): Result<Unit, PlayerAlreadyExists> =
        when {
            this.players.any { it.name == playerName } -> Err(PlayerAlreadyExists)
            else -> Ok(Unit)
        }

    object PlayerAlreadyExists
}

data class Player(
    val name: String,
    val currentPosition: Position
)

typealias GameId = String

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
