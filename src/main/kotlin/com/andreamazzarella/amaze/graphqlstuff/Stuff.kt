package com.andreamazzarella.amaze.graphqlstuff

import com.andreamazzarella.amaze.core.Floor
import com.andreamazzarella.amaze.core.GetAMaze
import com.andreamazzarella.amaze.core.HitAWall
import com.andreamazzarella.amaze.core.MakeAMaze
import com.andreamazzarella.amaze.core.Maze
import com.andreamazzarella.amaze.core.MazeNotFound
import com.andreamazzarella.amaze.core.OutsideMaze
import com.andreamazzarella.amaze.core.Position
import com.andreamazzarella.amaze.core.StepDirection
import com.andreamazzarella.amaze.core.TakeAStep
import com.andreamazzarella.amaze.core.TakeAStepError
import com.andreamazzarella.amaze.core.Wall
import com.andreamazzarella.amaze.persistence.MazeNotFoundError
import com.andreamazzarella.amaze.persistence.MazeRepository
import com.andreamazzarella.amaze.utils.Err
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.Result
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
import java.util.UUID

@Configuration
class Configuration() {

    @Bean
    fun dictionaryParser(): SchemaParserDictionary {
        return SchemaParserDictionary()
            .add(StepResultResponse.HitAWall::class)
            .add(StepResultResponse.MazeDoesNotExist::class)
            .add(StepResultResponse.NewPosition::class)
            .add(CellResponse.Wall::class)
            .add(CellResponse.Floor::class)
    }
}

////////////
// resolvers

@Component
class MyMaze(@Autowired val getAMaze: GetAMaze) : GraphQLQueryResolver {
    fun myMaze(mazeId: UUID): MazeInfoResponse {
        val getAMazeResult = getAMaze.doIt(mazeId)
        return toMazeInfoResponseFromResult(getAMazeResult)
    }
}

private fun toMazeInfoResponseFromResult(findMazeResult: Result<Maze, MazeNotFoundError>): MazeInfoResponse =
    when (findMazeResult) {
        is Ok -> toMazeInfoResponse(findMazeResult.okValue)
        is Err -> TODO()
    }

private fun toMazeInfoResponse(maze: Maze): MazeInfoResponse {
    return MazeInfoResponse(
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
class CreateAMaze(@Autowired val makeAMaze: MakeAMaze) : GraphQLMutationResolver {
    fun createAMaze(mazeRunner: String): MazeId = makeAMaze.doIt()
}

@Component
class TakeAStepInTheMaze(
    @Autowired val takeAStep: TakeAStep,
    @Autowired val mazeRepository: MazeRepository,
    @Autowired val mazesPublisher: MyMazesPublisher
) : GraphQLMutationResolver {
    fun takeAStep(mazeId: MazeId, stepDirection: StepDirectionRequest): StepResultResponse {
        val stepResult = takeAStep.doIt(mazeId, fromStepDirectionRequest(stepDirection))
        return toStepResultResponse(stepResult)
    }

    private fun toStepResultResponse(stepResult: Result<Position, TakeAStepError>): StepResultResponse =
        when (stepResult) {
            is Ok -> {
                mazesPublisher.emitter!!.onNext(mazeRepository.allMazes().map(::toMazeInfoResponse))
                StepResultResponse.NewPosition(toPositionResponse(stepResult.okValue))
            }
            is Err -> toStepResultErrorResponse(stepResult.errorValue)
        }

    private fun toStepResultErrorResponse(error: TakeAStepError): StepResultResponse =
        when (error.error) {
            MazeNotFound -> StepResultResponse.MazeDoesNotExist("could not find your maze")
            HitAWall -> StepResultResponse.HitAWall("you hit a wall")
        }

    private fun fromStepDirectionRequest(stepDirection: StepDirectionRequest): StepDirection =
        when (stepDirection) {
            StepDirectionRequest.UP -> StepDirection.UP
            StepDirectionRequest.RIGHT -> StepDirection.RIGHT
            StepDirectionRequest.DOWN -> StepDirection.DOWN
            StepDirectionRequest.LEFT -> StepDirection.LEFT
        }
}

private fun toPositionResponse(position: Position) = PositionResponse(position.x(), position.y())

typealias MazeId = UUID

////////////////////////
// data transfer objects

enum class StepDirectionRequest { UP, RIGHT, DOWN, LEFT }

data class MazeInfoResponse(private val maze: MazeResponse, private val yourPosition: PositionResponse)
data class MazeResponse(private val cells: List<CellResponse>)
sealed class CellResponse {
    abstract val position: PositionResponse

    data class Wall(override val position: PositionResponse) : CellResponse()
    data class Floor(override val position: PositionResponse) : CellResponse()
}

data class PositionResponse(private val x: Int, private val y: Int)
sealed class StepResultResponse {
    data class MazeDoesNotExist(val message: String) : StepResultResponse()
    data class HitAWall(val message: String) : StepResultResponse()
    data class NewPosition(val position: PositionResponse) : StepResultResponse()
}

////////////////////////
// subscriptions

@Component
class MazesInfoSubscription(@Autowired val mazesPublisher: MyMazesPublisher) : GraphQLSubscriptionResolver {
    fun allMazes(): Publisher<List<MazeInfoResponse>> = mazesPublisher.publisher
}

@Component
class MyMazesPublisher {
    final var emitter: ObservableEmitter<List<MazeInfoResponse>>? = null
    final val publisher: Flowable<List<MazeInfoResponse>>

    init {
        val myObservable = Observable.create<List<MazeInfoResponse>> { emitter -> this.emitter = emitter }

        val connectableObservable = myObservable.share().publish()
        connectableObservable.connect()

        publisher = connectableObservable.toFlowable(BackpressureStrategy.BUFFER)
    }
}
