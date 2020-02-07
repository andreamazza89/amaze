module MazeApi exposing
    ( Cell(..)
    , GameId
    , GameStatus
    , RowOfCells
    , Webdata(..)
    , gameStatusDecoder
    , gameSubscription
    , startAGame
    )

import Api.Mutation
import Api.Object exposing (GameStatus)
import Api.Object.Floor
import Api.Object.GameStatus
import Api.Object.Maze
import Api.Object.PlayerPosition
import Api.Object.Position
import Api.Object.Wall
import Api.Scalar
import Api.Subscription as Subscription
import Api.Union
import Api.Union.Cell
import Dict exposing (Dict)
import Graphql.Document
import Graphql.Http
import Graphql.Operation exposing (RootSubscription)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet, with)
import Json.Decode as Decode



-- This is the nice data I want to deal with in the app (similar to but not exactly what the api offers)


type alias RowOfCells =
    List Cell


type Cell
    = Wall
    | Floor -- ideally this will carry a list of players


type alias GameId =
    String


type alias GameStatus =
    { maze : List RowOfCells
    }


toGameId : Api.Scalar.Id -> GameId
toGameId (Api.Scalar.Id apiGameID) =
    apiGameID



-- This is web data


type Webdata data
    = Loading
    | Failed
    | Success data


fromResult : Result error data -> Webdata data
fromResult result =
    case result of
        Ok data ->
            Success data

        Err _ ->
            Failed



-- This is how the nice data gets into the app


type alias ApiGameStatus =
    { maze : ApiMaze
    , runners : List ApiRunner
    }


type alias ApiMaze =
    { cells : List ApiCell }


type alias ApiRunner =
    { name : String
    , position : ApiPosition
    }


type alias ApiPosition =
    { column : Int
    , row : Int
    }


type ApiCell
    = ApiWall ApiPosition
    | ApiFloor ApiPosition



-- Mutations
--  (Result error data -> Webdata data)  ->


startAGame : (Webdata GameId -> msg) -> Cmd msg
startAGame toMsg =
    Graphql.Http.mutationRequest
        "http://localhost:8080/graphql"
        Api.Mutation.startAGame
        |> Graphql.Http.send (Result.map toGameId >> fromResult >> toMsg)



-- Subscriptions


gameSubscription : GameId -> String
gameSubscription gameId =
    gameSubscriptionSelection gameId
        |> Graphql.Document.serializeSubscription



-- Decoders


gameStatusDecoder : GameId -> Decode.Decoder GameStatus
gameStatusDecoder id =
    gameSubscriptionSelection id
        |> SelectionSet.map mapApiGameStatus
        |> Graphql.Document.decoder


mapApiGameStatus : ApiGameStatus -> GameStatus
mapApiGameStatus apiGameStatus =
    GameStatus <| mapCellsIntoRows apiGameStatus.maze.cells



-- Selection sets


gameSubscriptionSelection : GameId -> SelectionSet ApiGameStatus RootSubscription
gameSubscriptionSelection gameId =
    Subscription.gameStatus { gameId = Api.Scalar.Id gameId } gameSelection


gameSelection : SelectionSet ApiGameStatus Api.Object.GameStatus
gameSelection =
    SelectionSet.succeed ApiGameStatus
        |> with (Api.Object.GameStatus.maze mazeSelection)
        |> with (Api.Object.GameStatus.playersPositions playerSelection)


playerSelection : SelectionSet ApiRunner Api.Object.PlayerPosition
playerSelection =
    SelectionSet.succeed ApiRunner
        |> with Api.Object.PlayerPosition.playerName
        |> with (Api.Object.PlayerPosition.position positionSelection)


positionSelection : SelectionSet ApiPosition Api.Object.Position
positionSelection =
    SelectionSet.succeed ApiPosition
        |> with Api.Object.Position.x
        |> with Api.Object.Position.y


mazeSelection : SelectionSet ApiMaze Api.Object.Maze
mazeSelection =
    SelectionSet.succeed ApiMaze
        |> with (Api.Object.Maze.cells cellSelection)


cellSelection : SelectionSet ApiCell Api.Union.Cell
cellSelection =
    Api.Union.Cell.fragments
        { onWall = wallSelection
        , onFloor = floorSelection
        }


wallSelection : SelectionSet ApiCell Api.Object.Wall
wallSelection =
    SelectionSet.succeed ApiWall
        |> with (Api.Object.Wall.position positionSelection)


floorSelection : SelectionSet ApiCell Api.Object.Floor
floorSelection =
    SelectionSet.succeed ApiFloor
        |> with (Api.Object.Floor.position positionSelection)



-- Helpers to map from the data the api returns to the nice data we want to deal with


mapCellsIntoRows : List ApiCell -> List RowOfCells
mapCellsIntoRows cells =
    cells
        |> List.foldl accumulateIntoMapOfRows Dict.empty
        |> Dict.map (\_ apiCells -> fromApiCells apiCells)
        |> Dict.values


accumulateIntoMapOfRows : ApiCell -> Dict Int (List ApiCell) -> Dict Int (List ApiCell)
accumulateIntoMapOfRows apiCell rows =
    let
        rowN =
            getRow apiCell
    in
    case Dict.get rowN rows of
        Just _ ->
            Dict.update rowN (Maybe.map (addIdemToTheEnd apiCell)) rows

        Nothing ->
            Dict.insert rowN [ apiCell ] rows


getRow : ApiCell -> Int
getRow cell_ =
    case cell_ of
        ApiWall position ->
            position.row

        ApiFloor position ->
            position.row


addIdemToTheEnd : a -> List a -> List a
addIdemToTheEnd thingToAdd thingToAddTo =
    thingToAddTo ++ [ thingToAdd ]


fromApiCells : List ApiCell -> List Cell
fromApiCells =
    List.map fromApiCell


fromApiCell : ApiCell -> Cell
fromApiCell apiCell =
    case apiCell of
        ApiFloor _ ->
            Floor

        ApiWall _ ->
            Wall
