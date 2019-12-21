port module Main exposing (main)

import Api.Enum.Direction
import Api.Mutation
import Api.Object
import Api.Object.MazeInfo
import Api.Object.Position
import Api.Scalar exposing (Id)
import Api.Subscription as Subscription
import Browser
import Graphql.Document
import Graphql.Http
import Graphql.Operation exposing (RootSubscription)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet(..), with)
import Html exposing (Html, button, div, input, p, text)
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
    { position : PositionInternal }


type alias PositionInternal =
    { x : Int
    , y : Int
    }



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


positionSelection : SelectionSet PositionInternal Api.Object.Position
positionSelection =
    SelectionSet.succeed PositionInternal
        |> with Api.Object.Position.x
        |> with Api.Object.Position.y



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
        ]


viewMazeInfo : MazeInfoInternal -> Html msg
viewMazeInfo info =
    p [] [ text <| "this is one maze; current x: " ++ String.fromInt info.position.x ++ ", y: " ++ String.fromInt info.position.y ]


controlAMaze : Html Msg
controlAMaze =
    div []
        [ input [ onInput MazeIdTyped ] []
        , button [ onClick <| MoveRunnerClicked UP ] [ text "UP" ]
        , button [ onClick <| MoveRunnerClicked DOWN ] [ text "DOWN" ]
        , button [ onClick <| MoveRunnerClicked LEFT ] [ text "LEFT" ]
        , button [ onClick <| MoveRunnerClicked RIGHT ] [ text "RIGHT" ]
        ]
