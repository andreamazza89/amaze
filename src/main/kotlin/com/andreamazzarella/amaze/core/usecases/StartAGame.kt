package com.andreamazzarella.amaze.core.usecases

import com.andreamazzarella.amaze.core.Game
import com.andreamazzarella.amaze.core.GameId
import com.andreamazzarella.amaze.persistence.GameRepository

object StartAGame {
    fun doIt(): GameId =GameRepository.save(Game())
}