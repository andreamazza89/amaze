port module Main exposing (main)

import Api.Enum.Direction
import Api.Mutation
import Api.Object
import Api.Object.Floor
import Api.Object.GameStatus
import Api.Object.Maze
import Api.Object.PlayerPosition
import Api.Object.Position
import Api.Object.Wall
import Api.Scalar exposing (Id)
import Api.Subscription as Subscription
import Api.Union
import Api.Union.Cell
import Browser
import Element
import Element.Background
import Element.Border
import Element.Input
import Graphql.Document
import Graphql.Http exposing (Error)
import Graphql.Operation exposing (RootSubscription)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet(..), with)
import Html exposing (Html, button, div, input, text)
import Html.Events exposing (onClick, onInput)
import Json.Decode as Decode



-- Webdata


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
    { controllingMazeId : String
    , gameId : Maybe (Webdata Api.Scalar.Id)
    , gameInfo : Maybe GameStatusInternal
    }


type Msg
    = MazesInformationReceived Api.Scalar.Id Decode.Value
    | MazeIdTyped String
    | MoveRunnerClicked StepDirection
    | TakeAStepResponseReceived
    | StartAGameClicked
    | StartAGameResponseReceived (Webdata Api.Scalar.Id)



-- Maze decoding things


type StepDirection
    = UP
    | DOWN
    | LEFT
    | RIGHT


type alias GameStatusInternal =
    { maze : MazeInternal
    , runners : List RunnerInternal
    }


type alias RunnerInternal =
    { name : String
    , position : PositionInternal
    }


type alias PositionInternal =
    { column : Int
    , row : Int
    }


type alias MazeInternal =
    { cells : List CellInternal }


type alias GameId =
    String


type CellInternal
    = Wall PositionInternal
    | Floor PositionInternal


getY : CellInternal -> Int
getY cell_ =
    case cell_ of
        Wall position ->
            position.row

        Floor position ->
            position.row



-- Init


init : () -> ( Model, Cmd Msg )
init _ =
    ( initialModel, Cmd.none )


initialModel : Model
initialModel =
    { controllingMazeId = ""
    , gameId = Nothing
    , gameInfo = Nothing
    }



-- Update


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        MazesInformationReceived gameId mazesInfo ->
            decodeMazesInfo gameId mazesInfo model

        MazeIdTyped mazeId ->
            ( { model | controllingMazeId = mazeId }, Cmd.none )

        MoveRunnerClicked stepDirection ->
            ( model, takeAStep model.controllingMazeId stepDirection )

        TakeAStepResponseReceived ->
            ( model, Cmd.none )

        StartAGameClicked ->
            ( model, startAGame )

        StartAGameResponseReceived response ->
            ( { model | gameId = Just response }, subscribeToGameIfOneWasCreated response )


decodeMazesInfo : Api.Scalar.Id -> Decode.Value -> Model -> ( Model, Cmd msg )
decodeMazesInfo gameId gameInfo model =
    case Decode.decodeValue (gameStatusDecoder gameId) gameInfo of
        Ok stuff_ ->
            ( { model | gameInfo = Just stuff_ }, Cmd.none )

        Err _ ->
            ( model, Cmd.none )


startAGame : Cmd Msg
startAGame =
    Graphql.Http.mutationRequest
        "http://localhost:8080/graphql"
        Api.Mutation.startAGame
        |> Graphql.Http.send (fromResult >> StartAGameResponseReceived)


takeAStep : String -> StepDirection -> Cmd Msg
takeAStep mazeId stepDirection =
    Cmd.none



--Graphql.Http.mutationRequest
--    "http://localhost:8080/graphql"
--    (Api.Mutation.takeAStep { mazeId = toApiMazeId mazeId, stepDirection = toApiStep stepDirection } (SelectionSet.succeed ()))
--    |> Graphql.Http.send (always TakeAStepResponseReceived)


toId : String -> Id
toId id =
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


gameStatusDecoder : Api.Scalar.Id -> Decode.Decoder GameStatusInternal
gameStatusDecoder id =
    Graphql.Document.decoder <| gameSelection id


gameSelection : Api.Scalar.Id -> SelectionSet GameStatusInternal RootSubscription
gameSelection gameId =
    Subscription.gameStatus { gameId = gameId } gameInfoSelection


gameInfoSelection : SelectionSet GameStatusInternal Api.Object.GameStatus
gameInfoSelection =
    SelectionSet.succeed GameStatusInternal
        |> with (Api.Object.GameStatus.maze mazeSelection)
        |> with (Api.Object.GameStatus.playersPositions playerSelection)


playerSelection : SelectionSet RunnerInternal Api.Object.PlayerPosition
playerSelection =
    SelectionSet.succeed RunnerInternal
        |> with Api.Object.PlayerPosition.playerName
        |> with (Api.Object.PlayerPosition.position positionSelection)


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
subscriptions model =
    case model.gameId of
        Just id ->
            case id of
                Success id_ ->
                    mazesInformationReceived <| MazesInformationReceived id_

                _ ->
                    Sub.none

        Nothing ->
            Sub.none


subscribeToGameIfOneWasCreated : Webdata Api.Scalar.Id -> Cmd Msg
subscribeToGameIfOneWasCreated response =
    case response of
        Success id ->
            subscribeToGameUpdates id

        _ ->
            Cmd.none


subscribeToGameUpdates : Api.Scalar.Id -> Cmd msg
subscribeToGameUpdates id =
    gameSelection id
        |> Graphql.Document.serializeSubscription
        |> gameUpdates


port mazesInformationReceived : (Decode.Value -> msg) -> Sub msg


port gameUpdates : String -> Cmd msg



-- View


view : Model -> Html Msg
view model =
    Element.layout [] <| startAGame_ model


startAGame_ : Model -> Element.Element Msg
startAGame_ model =
    case model.gameId of
        Just gameId ->
            case gameId of
                Loading ->
                    Element.text "creating a game ..."

                Failed ->
                    Element.text "failed to create a game - please refresh the page and try again"

                Success (Api.Scalar.Id id) ->
                    Element.column []
                        [ Element.text <| "GAME ID: " ++ id
                        , Maybe.map viewMaze2 model.gameInfo |> Maybe.withDefault Element.none
                        ]

        Nothing ->
            Element.Input.button []
                { onPress = Just StartAGameClicked
                , label = Element.text "start a game"
                }


viewMaze2 : GameStatusInternal -> Element.Element msg
viewMaze2 gameStatus =
    makeRows gameStatus.maze.cells []
        |> List.map viewRow2
        |> Element.column []


viewRow2 : List CellInternal -> Element.Element msg
viewRow2 row =
    Element.row [] <| List.map viewCell2 row


viewCell2 : CellInternal -> Element.Element msg
viewCell2 cell_ =
    case cell_ of
        Wall _ ->
            darkCell

        Floor _ ->
            lightCell


darkCell : Element.Element msg
darkCell =
    cell [ Element.Background.color <| Element.rgb255 0 0 0 ]


lightCell : Element.Element msg
lightCell =
    cell [ Element.Background.color <| Element.rgb255 255 255 255 ]


runnerCell : Element.Element msg
runnerCell =
    cell
        [ Element.Background.color <| Element.rgb255 51 204 51
        , Element.Border.rounded 200
        ]


cell : List (Element.Attribute msg) -> Element.Element msg
cell attributes =
    Element.el
        ([ Element.width (Element.fill |> Element.minimum 25)
         , Element.height (Element.fill |> Element.minimum 25)
         ]
            ++ attributes
        )
        Element.none


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


controlAMaze : Html Msg
controlAMaze =
    div []
        [ input [ onInput MazeIdTyped ] []
        , button [ onClick <| MoveRunnerClicked UP ] [ text "UP" ]
        , button [ onClick <| MoveRunnerClicked DOWN ] [ text "DOWN" ]
        , button [ onClick <| MoveRunnerClicked LEFT ] [ text "LEFT" ]
        , button [ onClick <| MoveRunnerClicked RIGHT ] [ text "RIGHT" ]
        ]
