package com.andreamazzarella.amaze.web

enum class StepDirectionRequest { NORTH, EAST, SOUTH, WEST }

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
    data class NewPosition(val position: PositionResponse) : StepResultResponse()
}

sealed class AddAPlayerResponse {
    data class Success(val message: String?) : AddAPlayerResponse()
    data class Failure(val message: String?) : AddAPlayerResponse()
}

typealias GameId = String

// Sample query objects

data class SampleType(
    val aString: String,
    val aNestedThing: AThing,
    val aListOfThings: List<AThing>
)

data class AThing(val theAnswer: Int)
