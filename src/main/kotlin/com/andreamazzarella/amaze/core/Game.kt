package com.andreamazzarella.amaze.core

data class Game(val mazes: List<Maze> = emptyList()) {
    fun id() = generateGameId()
}

typealias GameId = String

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
