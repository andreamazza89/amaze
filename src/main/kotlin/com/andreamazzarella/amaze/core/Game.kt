package com.andreamazzarella.amaze.core

import com.andreamazzarella.amaze.utils.Err
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.Result
import com.andreamazzarella.amaze.utils.andThen
import com.andreamazzarella.amaze.utils.map
import com.andreamazzarella.amaze.utils.mapError
import com.andreamazzarella.amaze.utils.pipe

data class Game(
    val id: GameId = generateGameId(),
    val maze: Maze = aMazeFromADrawing(DEFAULT_MAZE),
    val mazes: List<Maze> = emptyList(),
    private val players: List<Player> = emptyList()
) {

    // Query

    fun playersPositions(): List<Pair<String, Position>> =
        this.players.map { it.name to it.currentPosition }

    fun playerPosition(playerName: String): Result<Position, StepError> =
        players.find { it.name == playerName }
            .pipe { player ->
                if (player == null) {
                    Err(StepError.PlayerNotFound)
                } else {
                    Ok(player.currentPosition)
                }
            }

    // Update

    fun withMaze(maze: Maze) =
        this.copy(mazes = mazes + maze)

    fun addPlayer(playerName: String): Result<Game, PlayerAlreadyExists> =
        checkPlayerCanBeAdded(playerName)
            .map { this.copy(players = this.players + Player(playerName, maze.entrance)) }

    fun takeAStep(playerName: String, direction: StepDirection): Result<Game, StepError> =
        playerPosition(playerName)
            .andThen { currentPosition -> tryStepInMaze(maze, currentPosition, direction)}
            .map { newPosition -> this.updatePlayerPosition(playerName, newPosition) }

    private fun tryStepInMaze(
        maze: Maze,
        currentPosition: Position,
        direction: StepDirection
    ): Result<Position, StepError> =
        maze.takeAStep2(currentPosition, direction)
            .mapError { StepError.InvalidStep }

    // Helpers

    private fun checkPlayerCanBeAdded(playerName: String): Result<Unit, PlayerAlreadyExists> =
        when {
            this.players.any { it.name == playerName } -> Err(PlayerAlreadyExists)
            else -> Ok(Unit)
        }

    private fun updatePlayerPosition(playerName: String, newPosition: Position): Game =
        this.copy(players = players.map {
            if (it.name == playerName) {
                it.withNewPosition(newPosition)
            } else {
                it
            }
        })

    // Errors

    object PlayerAlreadyExists

    sealed class StepError {
        object PlayerNotFound : StepError()
        object InvalidStep : StepError()
    }
}

data class Player(
    val name: String,
    val currentPosition: Position
) {
    fun withNewPosition(position: Position) = this.copy(currentPosition = position)
}

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
