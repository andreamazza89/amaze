package com.andreamazzarella.amaze.web

import java.util.UUID

enum class StepDirectionRequest { NORTH, EAST, SOUTH, WEST }
typealias StepDirectionResponse = StepDirectionRequest

data class GameStatusResponse(
    val maze: MazeResponse,
    val playersPositions: List<PlayerPositionResponse>
)

data class MazeResponse(private val cells: List<CellResponse>)

data class PlayerPositionResponse(
    val playerName: String,
    val position: PositionResponse
)

sealed class CellResponse {
    abstract val position: PositionResponse

    data class Wall(override val position: PositionResponse) : CellResponse()
    data class Floor(override val position: PositionResponse) : CellResponse()
}

data class PositionResponse(
    val x: Int,
    val y: Int
)

sealed class StepResultResponse {
    data class GameDoesNotExist(val message: String) : StepResultResponse()
    data class HitAWall(val message: String) : StepResultResponse()
    data class PlayerNotInThisGame(val message: String) : StepResultResponse()
    data class TokenIsNotValid(val message: String) : StepResultResponse()
    data class PlayerGotOut(val message: String) : StepResultResponse()
    data class NewPosition(val position: PositionResponse) : StepResultResponse()
}

sealed class AddAPlayerResponse {
    data class Success(val token: UUID) : AddAPlayerResponse()
    data class Failure(val message: String?) : AddAPlayerResponse()
}

sealed class PlayerStatusResponse {
    data class StillIn(val directionsAvailable: List<StepDirectionResponse>) : PlayerStatusResponse()
    data class GotOut(val message: String?) : PlayerStatusResponse()
    data class Failure(val message: String?) : PlayerStatusResponse()
}

typealias GameId = String

// Sample query objects

data class SampleType(
    val aString: String,
    val anotherString: String,
    val aNestedThing: AThing,
    val aListOfThings: List<AThing>
)

data class AThing(val theAnswer: Int)
