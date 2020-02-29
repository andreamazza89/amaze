package com.andreamazzarella.amaze.graphqlstuff

import com.andreamazzarella.amaze.core.Floor
import com.andreamazzarella.amaze.core.Game
import com.andreamazzarella.amaze.core.Maze
import com.andreamazzarella.amaze.core.OutsideMaze
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.StepDirection
import com.andreamazzarella.amaze.core.Wall
import com.andreamazzarella.amaze.core.usecases.AddAPlayer
import com.andreamazzarella.amaze.core.usecases.GetAGame
import com.andreamazzarella.amaze.core.usecases.GetAMaze
import com.andreamazzarella.amaze.core.usecases.HitAWall
import com.andreamazzarella.amaze.core.usecases.MazeNotFound
import com.andreamazzarella.amaze.core.usecases.StartAGame
import com.andreamazzarella.amaze.core.usecases.TakeAStep2
import com.andreamazzarella.amaze.core.usecases.TakeAStep2Error
import com.andreamazzarella.amaze.core.usecases.TakeAStepError
import com.andreamazzarella.amaze.persistence.GameRepository
import com.andreamazzarella.amaze.persistence.MazeNotFoundError
import com.andreamazzarella.amaze.utils.Err
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.Result
import com.andreamazzarella.amaze.utils.okOrFail
import com.andreamazzarella.amaze.utils.pipe
import com.andreamazzarella.amaze.utils.runOnOk
import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver
import com.coxautodev.graphql.tools.SchemaParserDictionary
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import org.reactivestreams.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

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
            .add(CellResponse.Wall::class)
            .add(CellResponse.Floor::class)
    }
}

////////////
// resolvers

@Component
class GameStatus(@Autowired val getAMaze: GetAMaze) : GraphQLQueryResolver {
    fun gameStatus(gameId: GameId): GameStatusResponse =
        GetAGame.doIt(gameId)
            .okOrFail() // Game Status Response should be the union of a successful response and a game not found
            .pipe(::toGameStatusResponse)
}

@Component
class GamesAvailable : GraphQLQueryResolver {
    fun gamesAvailable(): List<GameId> =
        GameRepository.findAll().map(Game::id)
}

private fun toMazeInfoResponseFromResult(findMazeResult: Result<Maze, MazeNotFoundError>): GameInfoResponse =
    when (findMazeResult) {
        is Ok -> toMazeInfoResponse(findMazeResult.okValue)
        is Err -> TODO()
    }

private fun toMazeInfoResponse(maze: Maze): GameInfoResponse {
    return GameInfoResponse(
        MazeResponse(toCellsResponse(maze)),
        toPositionResponse(maze.currentPosition)
    )
}

private fun toCellsResponse(maze: Maze): List<CellResponse> =
    maze.cells.map { cell ->
        when (cell) {
            is Wall -> CellResponse.Wall(toPositionResponse(cell.position))
            is Floor -> CellResponse.Floor(toPositionResponse(cell.position))
            is OutsideMaze -> TODO()
        }
    }

@Component
class StartAGameResolver() : GraphQLMutationResolver {
    fun startAGame(): GameId = StartAGame.doIt()
}

@Component
class AddAPlayerResolver(@Autowired val gamePublisherThing: GamePublishersThing) : GraphQLMutationResolver {
    fun addAPlayerToAGame(gameId: GameId, playerName: String): AddAPlayerResponse {
        val response = AddAPlayer.doIt(gameId, playerName)
            .pipe {
                when (it) {
                    is Ok -> AddAPlayerResponse.Success("player added")
                    is Err -> AddAPlayerResponse.Failure("could not add player")
                }
            }
        GameRepository.find(gameId).runOnOk { gamePublisherThing.emitForGame(gameId, toGameStatusResponse(it)) }
        return response
    }

}

fun toGameStatusResponse(game: Game) =
    GameStatusResponse(
        MazeResponse(toCellsResponse(game.maze)),
        game.playersPositions().map(::toPlayerPositionResponse)
    )

fun toPlayerPositionResponse(player: Pair<String, Position>) =
    PlayerPositionResponse(player.first, toPositionResponse(player.second))


typealias GameId = String

private fun toPositionResponse(position: Position) = PositionResponse(position.x(), position.y())

@Component
class TakeAStepTwo(@Autowired val gamePublishersBuilder: GamePublishersThing) : GraphQLMutationResolver {
    fun takeAStepOnTheMap(gameId: GameId, playerName: String, stepDirection: StepDirectionRequest): StepResultResponse =
        TakeAStep2.doIt(gameId, playerName, fromStepDirectionRequest(stepDirection))
            .pipe { newPosition ->
                when (newPosition) {
                    is Ok -> {
                        GameRepository.find(gameId).runOnOk { gamePublishersBuilder.emitForGame(gameId, toGameStatusResponse(it)) }
                        StepResultResponse.NewPosition(toPositionResponse(newPosition.okValue))
                    }
                    is Err -> toStepResultErrorResponse2(newPosition.errorValue)
                }
            }

    private fun toStepResultErrorResponse(error: TakeAStepError): StepResultResponse =
        when (error.error) {
            MazeNotFound -> StepResultResponse.GameDoesNotExist("could not find your maze")
            HitAWall -> StepResultResponse.HitAWall("you hit a wall")
        }

    private fun toStepResultErrorResponse2(error: TakeAStep2Error): StepResultResponse =
        when (error) {
            TakeAStep2Error.GameDoesNotExist -> StepResultResponse.GameDoesNotExist("could not find your maze")
            TakeAStep2Error.InvalidStep -> StepResultResponse.HitAWall("you hit a wall")
            TakeAStep2Error.PlayerNotInThisGame -> TODO()
        }

    private fun fromStepDirectionRequest(stepDirection: StepDirectionRequest): StepDirection = when (stepDirection) {
            StepDirectionRequest.NORTH -> StepDirection.UP
            StepDirectionRequest.EAST -> StepDirection.RIGHT
            StepDirectionRequest.SOUTH -> StepDirection.DOWN
            StepDirectionRequest.WEST -> StepDirection.LEFT
        }
}

////////////////////////
// data transfer objects

enum class StepDirectionRequest { NORTH, EAST, SOUTH, WEST }

data class GameInfoResponse(private val maze: MazeResponse, private val yourPosition: PositionResponse)
data class GameStatusResponse(private val maze: MazeResponse, private val playersPositions: List<PlayerPositionResponse>)
data class MazeResponse(private val cells: List<CellResponse>)
sealed class CellResponse {
    abstract val position: PositionResponse

    data class Wall(override val position: PositionResponse) : CellResponse()
    data class Floor(override val position: PositionResponse) : CellResponse()
}

data class PositionResponse(private val x: Int, private val y: Int)
data class PlayerPositionResponse(private val playerName: String, private val position: PositionResponse)

sealed class StepResultResponse {
    data class GameDoesNotExist(val message: String) : StepResultResponse()
    data class HitAWall(val message: String) : StepResultResponse()
    data class PlayerNotInThisGame(val message: String) : StepResultResponse()
    data class NewPosition(val position: PositionResponse) : StepResultResponse()
}

sealed class AddAPlayerResponse {
    data class Success(val message: String?) : AddAPlayerResponse()
    data class Failure(val message: String?) : AddAPlayerResponse()
}

////////////////////////
// subscriptions

@Component
class GameSubscription(@Autowired val gamePublishersBuilder: GamePublishersThing) : GraphQLSubscriptionResolver {
    fun gameStatus(gameId: GameId): Publisher<GameStatusResponse> = gamePublishersBuilder.publisherForGameId(gameId)
}

@Component
class GamePublishersThing {
    private val publishers: MutableMap<GameId, MyGamePublisher> = mutableMapOf()

    fun publisherForGameId(gameId: GameId): Publisher<GameStatusResponse> =
        publishers[gameId]?.publisher ?: initialisePublisher(gameId).publisher

    private fun initialisePublisher(gameId: GameId): MyGamePublisher {
        val publisher = MyGamePublisher()
        publishers[gameId] = publisher
        return publisher
    }

    fun emitForGame(gameId: GameId, messageToEmit: GameStatusResponse) {
        publishers[gameId]!!.emitter!!.onNext(messageToEmit)
    }
}

class MyGamePublisher {
    var emitter: ObservableEmitter<GameStatusResponse>? = null
    val publisher: Flowable<GameStatusResponse>

    init {
        val myObservable = Observable.create<GameStatusResponse> { emitter -> this.emitter = emitter }

        val connectableObservable = myObservable.share().publish()
        connectableObservable.connect()

        publisher = connectableObservable.toFlowable(BackpressureStrategy.BUFFER)
    }
}
