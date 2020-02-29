package com.andreamazzarella.amaze.web

import com.andreamazzarella.amaze.core.Game
import com.andreamazzarella.amaze.core.usecases.AddAPlayer
import com.andreamazzarella.amaze.core.usecases.GetAGame
import com.andreamazzarella.amaze.core.usecases.StartAGame
import com.andreamazzarella.amaze.core.usecases.TakeAStep
import com.andreamazzarella.amaze.persistence.GameRepository
import com.andreamazzarella.amaze.utils.okOrFail
import com.andreamazzarella.amaze.utils.pipe
import com.andreamazzarella.amaze.utils.runOnOk
import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver
import org.reactivestreams.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

// Queries

@Component
class Queries : GraphQLQueryResolver {

    fun gameStatus(gameId: GameId): GameStatusResponse =
        GetAGame.doIt(gameId)
            .okOrFail() // Should update the response type to cater for games not found rather than throw
            .pipe(Mappers::toGameStatusResponse)

    fun gamesAvailable(): List<GameId> =
        GameRepository.findAll().map(Game::id)

    fun sampleQuery(): SampleType =
        SampleType(
            aString = "hello",
            aNestedThing = AThing(42),
            aListOfThings = listOf(AThing(42), AThing(43))
        )
}

// Mutations

@Component
class Mutations(@Autowired val gamePublishersBuilder: GamePublishers) : GraphQLMutationResolver {

    fun startAGame(): GameId = StartAGame.doIt()

    fun addAPlayerToAGame(gameId: GameId, playerName: String): AddAPlayerResponse =
        AddAPlayer.doIt(gameId, playerName)
            .runOnOk { gamePublishersBuilder.triggerGameStatusUpdate(gameId) }
            .pipe(Mappers::toAddAPlayerResponse)

    fun takeAStep(gameId: GameId, playerName: String, stepDirection: StepDirectionRequest): StepResultResponse =
        Mappers.toStepDirection(stepDirection)
            .pipe { TakeAStep.doIt(gameId, playerName, it) }
            .runOnOk { gamePublishersBuilder.triggerGameStatusUpdate(gameId) }
            .pipe { Mappers.toTakeAStepResponse(it) }
}

// Subscriptions

@Component
class Subscriptions(@Autowired val gamePublishersBuilder: GamePublishers) : GraphQLSubscriptionResolver {

    fun gameStatus(gameId: GameId): Publisher<GameStatusResponse> =
        gamePublishersBuilder.publisherForGame(gameId)
}
