package com.andreamazzarella.amaze.web

import com.andreamazzarella.amaze.persistence.GameRepository
import com.andreamazzarella.amaze.utils.runOnOk
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import org.reactivestreams.Publisher
import org.springframework.stereotype.Component

@Component
class GamePublishers {
    private val publishers: MutableMap<GameId, GamePublisher> = mutableMapOf()

    fun publisherForGame(gameId: GameId): Publisher<GameStatusResponse> =
        publishers[gameId]?.publisher ?: initialisePublisher(gameId).publisher

    fun triggerGameStatusUpdate(gameId: GameId) {
        GameRepository
            .find(gameId)
            .runOnOk {
                emitterForGame(gameId).onNext(Mappers.toGameStatusResponse(it))
            }
    }

    private fun emitterForGame(gameId: GameId): ObservableEmitter<GameStatusResponse> =
        publishers[gameId]?.emitter ?: initialisePublisher(gameId).emitter!!

    private fun initialisePublisher(gameId: GameId): GamePublisher {
        val publisher = GamePublisher()
        publishers[gameId] = publisher
        return publisher
    }
}

class GamePublisher {
    var emitter: ObservableEmitter<GameStatusResponse>? = null
    val publisher: Flowable<GameStatusResponse>

    init {
        val observable = Observable.create<GameStatusResponse> { emitter -> this.emitter = emitter }

        val connectableObservable = observable.share().publish()
        connectableObservable.connect()

        publisher = connectableObservable.toFlowable(BackpressureStrategy.BUFFER)
    }
}
