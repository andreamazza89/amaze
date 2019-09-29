package com.andreamazzarella.amaze.graphqlstuff

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver
import com.coxautodev.graphql.tools.SchemaParserDictionary
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import org.joda.time.DateTime
import org.reactivestreams.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.concurrent.fixedRateTimer

@Configuration
class Configuration() {

    @Bean
    fun dictionaryParser(): SchemaParserDictionary {
        return SchemaParserDictionary()
            .add(StepResultResponse.HitAWall::class)
            .add(StepResultResponse.MazeDoesNotExist::class)
            .add(StepResultResponse.NewPosition::class)
    }
}

////////////
// resolvers

@Component
class MyMaze: GraphQLQueryResolver {
    fun myMaze(mazeId: UUID): MazeInfoResponse {
        return MazeInfoResponse(MazeResponse(42), PositionResponse(42, 42))
    }
}

@Component
class SaySomething(@Autowired val chatPublisher: MyChatPublisher) : GraphQLMutationResolver {
    fun saySomething(message: String): String {
        chatPublisher.emitter!!.onNext(message)
        return "Well said"
    }
}

@Component
class Mutation : GraphQLMutationResolver {
    fun createAMaze(mazeRunner: String): MazeId = UUID.randomUUID()
}

@Component
class TakeAStep : GraphQLMutationResolver {
    fun takeAStep(mazeId: MazeId, stepDirection: StepDirectionRequest): StepResultResponse {
        // to domain action
        // attemptStep
        // convert result
        return StepResultResponse.HitAWall("You hit a wall")
    }
}

typealias MazeId = UUID

////////////////////////
// data transfer objects


enum class StepDirectionRequest { UP, RIGHT, DOWN, LEFT }

data class MazeInfoResponse(private val maze: MazeResponse, private val yourPosition: PositionResponse)
data class MazeResponse(private val rows: Int)
data class PositionResponse(private val x: Int, private val y: Int)
sealed class StepResultResponse {
    data class MazeDoesNotExist(val message: String) : StepResultResponse()
    data class HitAWall(val message: String) : StepResultResponse()
    data class NewPosition(val position: PositionResponse) : StepResultResponse()
}

////////////////////////
// subscriptions

@Component
class SecondsSubscription : GraphQLSubscriptionResolver {
    fun tellMeSeconds(): Publisher<Int> = MySecondsPublisher().publisher
}

@Component
class MySecondsPublisher {
    final val publisher: Flowable<Int>

    init {
        val myObservable = Observable.create<Int> { emitter ->
            fixedRateTimer(initialDelay = 100, period = 100) {
                emitter.onNext(DateTime.now().secondOfDay)
            }
        }

        val connectableObservable = myObservable.share().publish()
        connectableObservable.connect()

        publisher = connectableObservable.toFlowable(BackpressureStrategy.BUFFER)
    }
}

@Component
class ChatSubscription(@Autowired val chatPublisher: MyChatPublisher) : GraphQLSubscriptionResolver {
    fun tellMeWhatOthersSay(): Publisher<String> = chatPublisher.publisher
}

@Component
class MyChatPublisher {
    final var emitter: ObservableEmitter<String>? = null
    final val publisher: Flowable<String>

    init {
        val myObservable = Observable.create<String> { emitter -> this.emitter = emitter }

        val connectableObservable = myObservable.share().publish()
        connectableObservable.connect()

        publisher = connectableObservable.toFlowable(BackpressureStrategy.BUFFER)
    }
}


