package com.andreamazzarella.amaze.graphqlstuff

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import org.springframework.stereotype.Component
import java.util.UUID
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import kotlin.concurrent.fixedRateTimer

////////////
// resolvers

@Component
class Query: GraphQLQueryResolver {
    fun myMaze(mazeId: UUID): MazeResponse {
        return MazeResponse(PositionResponse(42, 42))
    }
}

@Component
class SaySomething(@Autowired val chatPublisher: MyChatPublisher): GraphQLMutationResolver {
    fun saySomething(message: String): String {
        chatPublisher.emitter!!.onNext(message)
        return "Well said"
    }
}


@Component
class Mutation: GraphQLMutationResolver {
    fun createAMaze(mazeRunner: String): MazeId {
        return UUID.randomUUID()
    }
}

typealias MazeId = UUID

////////////////////////
// data transfer objects

data class MazeResponse(private val yourPosition: PositionResponse)
data class PositionResponse(private val x: Int, private val y: Int)

////////////////////////
// subscriptions

@Component
class SecondsSubscription: GraphQLSubscriptionResolver {
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
class ChatSubscription(@Autowired val chatPublisher: MyChatPublisher): GraphQLSubscriptionResolver {
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


