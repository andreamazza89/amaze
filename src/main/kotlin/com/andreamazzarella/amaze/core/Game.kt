package com.andreamazzarella.amaze.core

data class Game(val id: GameId, val mazes: List<Maze> = emptyList()) {
    fun withMaze(maze: Maze) = this.copy(mazes = mazes + maze)

    fun updateMaze(maze: Maze) = this.copy(mazes = mazes.filter { it.id != maze.id } + maze)
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
