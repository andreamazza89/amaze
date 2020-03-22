package com.andreamazzarella.amaze.web

import com.coxautodev.graphql.tools.SchemaParserDictionary
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Configuration() {

    @Bean
    fun dictionaryParser(): SchemaParserDictionary {
        return SchemaParserDictionary()
            .add(StepResultResponse.HitAWall::class)
            .add(StepResultResponse.GameDoesNotExist::class)
            .add(StepResultResponse.PlayerNotInThisGame::class)
            .add(StepResultResponse.NewPosition::class)
            .add(AddAPlayerResponse.Success::class)
            .add(AddAPlayerResponse.Failure::class)
            .add(PlayerStatusResponse.StillIn::class)
            .add(PlayerStatusResponse.GotOut::class)
            .add(PlayerStatusResponse.Failure::class)
            .add(CellResponse.Wall::class)
            .add(CellResponse.Floor::class)
    }
}
