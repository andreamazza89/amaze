package com.andreamazzarella.amaze.web

import java.util.UUID

object PlayerTokens {
    private var tokens: MutableMap<Pair<GameId, PlayerName>, Token> = mutableMapOf()

    fun generate(gameId: GameId, playerName: PlayerName): Token {
        val token = UUID.randomUUID()
        tokens[Pair(gameId, playerName)] = token
        return token
    }

    fun checkPlayerIsLegit(gameId: GameId, playerName: PlayerName, tokenGiven: Token): Boolean =
        tokens[Pair(gameId, playerName)] == tokenGiven
}

typealias PlayerName = String
typealias Token = UUID
