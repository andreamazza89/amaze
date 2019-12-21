port module Main exposing (main)

import Api.Enum.Direction
import Api.Mutation
import Api.Object
import Api.Object.Floor
import Api.Object.Maze
import Api.Object.MazeInfo
import Api.Object.Position
import Api.Object.Wall
import Api.Scalar exposing (Id)
import Api.Subscription as Subscription
import Api.Union
import Api.Union.Cell
import Browser
import Element
import Graphql.Document
import Graphql.Http
import Graphql.Operation exposing (RootSubscription)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet(..), with)
import Html exposing (Html, button, div, input, text)
import Html.Events exposing (onClick, onInput)
import Json.Decode as Decode



-- Program


main : Program () Model Msg
main =
    Browser.element
        { init = init
        , update = update
        , view = view
        , subscriptions = subscriptions
        }



-- Model


type alias Model =
    { mazes : List MazeInfoInternal
    , controllingMazeId : String
    }


type Msg
    = MazesInformationReceived Decode.Value
    | SubscribeToMazeUpdates
    | MazeIdTyped String
    | MoveRunnerClicked StepDirection
    | TakeAStepResponseReceived


type StepDirection
    = UP
    | DOWN
    | LEFT
    | RIGHT


type alias MazeInfoInternal =
    { runnerPosition : PositionInternal
    , maze : MazeInternal
    }


type alias PositionInternal =
    { x : Int
    , y : Int
    }


type alias MazeInternal =
    { cells : List CellInternal }


type CellInternal
    = Wall PositionInternal
    | Floor PositionInternal


getY : CellInternal -> Int
getY cell =
    case cell of
        Wall position ->
            position.y

        Floor position ->
            position.y



-- Init


init : () -> ( Model, Cmd Msg )
init _ =
    ( initialModel, Cmd.none )


initialModel : Model
initialModel =
    { mazes = []
    , controllingMazeId = ""
    }



-- Update


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        MazesInformationReceived mazeInfo ->
            case Decode.decodeValue mazeInfoDecoder mazeInfo of
                Ok mazesInfo ->
                    ( { model | mazes = mazesInfo }, Cmd.none )

                Err _ ->
                    ( model, Cmd.none )

        SubscribeToMazeUpdates ->
            ( model, subscribeToMazesUpdates )

        MazeIdTyped mazeId ->
            ( { model | controllingMazeId = mazeId }, Cmd.none )

        MoveRunnerClicked stepDirection ->
            ( model, takeAStep model.controllingMazeId stepDirection )

        TakeAStepResponseReceived ->
            ( model, subscribeToMazesUpdates )


takeAStep : String -> StepDirection -> Cmd Msg
takeAStep mazeId stepDirection =
    Graphql.Http.mutationRequest
        "http://localhost:8080/graphql"
        (Api.Mutation.takeAStep { mazeId = toApiMazeId mazeId, stepDirection = toApiStep stepDirection } (SelectionSet.succeed ()))
        |> Graphql.Http.send (always TakeAStepResponseReceived)


toApiMazeId : String -> Id
toApiMazeId id =
    Api.Scalar.Id id


toApiStep : StepDirection -> Api.Enum.Direction.Direction
toApiStep step =
    case step of
        UP ->
            Api.Enum.Direction.Up

        DOWN ->
            Api.Enum.Direction.Down

        LEFT ->
            Api.Enum.Direction.Left

        RIGHT ->
            Api.Enum.Direction.Right


mazeInfoDecoder : Decode.Decoder (List MazeInfoInternal)
mazeInfoDecoder =
    Graphql.Document.decoder mazesSelection


mazesSelection : SelectionSet (List MazeInfoInternal) RootSubscription
mazesSelection =
    Subscription.allMazes mazeInfoSelection


mazeInfoSelection : SelectionSet MazeInfoInternal Api.Object.MazeInfo
mazeInfoSelection =
    SelectionSet.succeed MazeInfoInternal
        |> with (Api.Object.MazeInfo.yourPosition positionSelection)
        |> with (Api.Object.MazeInfo.maze mazeSelection)


positionSelection : SelectionSet PositionInternal Api.Object.Position
positionSelection =
    SelectionSet.succeed PositionInternal
        |> with Api.Object.Position.x
        |> with Api.Object.Position.y


mazeSelection : SelectionSet MazeInternal Api.Object.Maze
mazeSelection =
    SelectionSet.succeed MazeInternal
        |> with (Api.Object.Maze.cells cellSelection)


cellSelection : SelectionSet CellInternal Api.Union.Cell
cellSelection =
    Api.Union.Cell.fragments
        { onWall = wallSelection
        , onFloor = floorSelection
        }


wallSelection : SelectionSet CellInternal Api.Object.Wall
wallSelection =
    SelectionSet.succeed Wall
        |> with (Api.Object.Wall.position positionSelection)


floorSelection : SelectionSet CellInternal Api.Object.Floor
floorSelection =
    SelectionSet.succeed Floor
        |> with (Api.Object.Floor.position positionSelection)



-- Subscriptions


subscriptions : Model -> Sub Msg
subscriptions _ =
    mazesInformationReceived MazesInformationReceived


subscribeToMazesUpdates : Cmd msg
subscribeToMazesUpdates =
    mazeUpdates <| Graphql.Document.serializeSubscription mazesSelection


port mazesInformationReceived : (Decode.Value -> msg) -> Sub msg


port mazeUpdates : String -> Cmd msg



-- View


view : Model -> Html Msg
view model =
    div []
        [ controlAMaze
        , button [ onClick SubscribeToMazeUpdates ] [ text "subscribe to maze updates" ]
        , div [] <| List.map viewMazeInfo model.mazes
        , Element.layout [] <| Element.text "HIII"
        ]


viewMazeInfo : MazeInfoInternal -> Html msg
viewMazeInfo info =
    div []
        [ text <| "this is one maze; current x: " ++ String.fromInt info.runnerPosition.x ++ ", y: " ++ String.fromInt info.runnerPosition.y
        , viewMaze info.runnerPosition <| makeRows info.maze.cells []
        ]


makeRows : List CellInternal -> List (List CellInternal) -> List (List CellInternal)
makeRows allCells cellsIntoRows =
    if List.isEmpty allCells then
        cellsIntoRows

    else
        let
            nextRow =
                List.filter (\c -> getY c == getY (Maybe.withDefault (Floor (PositionInternal 2 3)) (List.head allCells))) allCells

            remainingCells =
                List.filter (\c -> getY c /= getY (Maybe.withDefault (Floor (PositionInternal 2 3)) (List.head allCells))) allCells
        in
        makeRows remainingCells (cellsIntoRows ++ [ nextRow ])


viewMaze : PositionInternal -> List (List CellInternal) -> Html msg
viewMaze runnerPosition rows =
    div [] <| List.map (viewRow runnerPosition) rows


viewRow : PositionInternal -> List CellInternal -> Html msg
viewRow runnerPosition row =
    div [] <| List.map (viewCell runnerPosition) row


viewCell : PositionInternal -> CellInternal -> Html msg
viewCell runnerPosition cell =
    case cell of
        Wall _ ->
            text "x"

        Floor position ->
            if runnerPosition == position then
                text "_"

            else
                text "o"


controlAMaze : Html Msg
controlAMaze =
    div []
        [ input [ onInput MazeIdTyped ] []
        , button [ onClick <| MoveRunnerClicked UP ] [ text "UP" ]
        , button [ onClick <| MoveRunnerClicked DOWN ] [ text "DOWN" ]
        , button [ onClick <| MoveRunnerClicked LEFT ] [ text "LEFT" ]
        , button [ onClick <| MoveRunnerClicked RIGHT ] [ text "RIGHT" ]
        ]
