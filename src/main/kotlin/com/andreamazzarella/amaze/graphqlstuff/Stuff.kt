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
import io.reactivex.internal.operators.single.SingleInternalHelper.toFlowable
import io.reactivex.observables.ConnectableObservable
import io.reactivex.ObservableEmitter
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
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
class Mutation: GraphQLMutationResolver {
    fun createAMaze(mazeRunner: String): MazeId {
        return UUID.randomUUID()
    }
}

@Component
class Subscription: GraphQLSubscriptionResolver {
    fun tellMeSeconds(): Publisher<Int> = CommentPublisher().publisher
}

typealias MazeId = UUID

////////////////////////
// data transfer objects

data class MazeResponse(private val yourPosition: PositionResponse)
data class PositionResponse(private val x: Int, private val y: Int)

@Component
class CommentPublisher {

    val publisher: Flowable<Int>

    private var emitter: ObservableEmitter<Int>? = null

    init {
        val commentUpdateObservable = Observable.create<Int> { emitter ->
            fixedRateTimer(name = "hello-timer",
                initialDelay = 100, period = 100) {
                emitter.onNext(DateTime.now().secondOfDay)
            }
        }

        val connectableObservable = commentUpdateObservable.share().publish()
        connectableObservable.connect()

        publisher = connectableObservable.toFlowable(BackpressureStrategy.BUFFER)
    }

    fun publish(note: Int) {
        emitter!!.onNext(42)
    }
}