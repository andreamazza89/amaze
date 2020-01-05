package com.andreamazzarella.amaze.core

import com.andreamazzarella.amaze.utils.Err
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.Result
import com.andreamazzarella.amaze.utils.map

data class Game(
    val id: GameId = generateGameId(),
    private val maze: Maze = aMazeFromADrawing(DEFAULT_MAZE),
    val mazes: List<Maze> = emptyList(),
    private val players: List<Player> = emptyList()
) {

    // Query

    fun playerPositions(): List<Pair<String, Position>> =
        this.players.map { it.name to it.currentPosition }

    // Update

    fun withMaze(maze: Maze) =
        this.copy(mazes = mazes + maze)

    fun updateMaze(maze: Maze) =
        this.copy(mazes = mazes.filter { it.id != maze.id } + maze)

    fun addPlayer(playerName: String): Result<Game, PlayerAlreadyExists> =
        checkPlayerCanBeAdded(playerName)
            .map { this.copy(players = this.players + Player(playerName, maze.entrance)) }

    // Helpers

    private fun checkPlayerCanBeAdded(playerName: String): Result<Unit, PlayerAlreadyExists> =
        when {
            this.players.any { it.name == playerName } -> Err(PlayerAlreadyExists)
            else -> Ok(Unit)
        }

    // Errors

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
