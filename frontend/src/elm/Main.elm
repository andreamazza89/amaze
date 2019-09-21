port module Main exposing (main)

import Api.Object as Object
import Api.Object.Maze as Maze
import Api.Object.Position as Position
import Api.Query as Query
import Api.Scalar exposing (Id(..))
import Api.Subscription as Subscription
import Browser
import Graphql.Document
import Graphql.Http
import Graphql.Operation exposing (RootQuery)
import Graphql.SelectionSet as SelectionSet
import Html exposing (Html, button, div, p, text)
import Html.Events exposing (onClick)
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
    { position : Maybe Position
    , seconds : Maybe Int
    }


type alias Position =
    { x : Int
    , y : Int
    }


type Msg
    = GetPositionClicked
    | PositionResponseRecevied (Result (Graphql.Http.Error (Maybe Position)) (Maybe Position))
    | SecondsResponseReceived String
    | TellMeSeconds



-- Init


init : () -> ( Model, Cmd Msg )
init _ =
    ( initialModel, Cmd.none )


initialModel : Model
initialModel =
    { position = Nothing
    , seconds = Nothing
    }



-- Update


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        GetPositionClicked ->
            ( model, getPosition )

        PositionResponseRecevied (Ok response) ->
            ( { model | position = response }, Cmd.none )

        PositionResponseRecevied (Err _) ->
            ( model, Cmd.none )

        TellMeSeconds ->
            ( model, tellMeSeconds <| Graphql.Document.serializeSubscription Subscription.tellMeSeconds )

        SecondsResponseReceived response ->
            ( { model | seconds = decodeSeconds response }, Cmd.none )


getPosition : Cmd Msg
getPosition =
    myMazePosition
        |> Graphql.Http.queryRequest "http://localhost:8080/graphql"
        |> Graphql.Http.send PositionResponseRecevied



-- Graphql


port tellMeSeconds : String -> Cmd msg


port secondsReceived : (String -> msg) -> Sub msg


myMazePosition : SelectionSet.SelectionSet (Maybe Position) RootQuery
myMazePosition =
    Query.myMaze { mazeId = Id "f0f57c20-0b6f-4e92-8438-8150549c574d" } position


position : SelectionSet.SelectionSet Position Object.Maze
position =
    SelectionSet.map2 Position
        (Maze.yourPosition Position.x)
        (Maze.yourPosition Position.y)


decodeSeconds : String -> Maybe Int
decodeSeconds response =
    response
        |> Decode.decodeString (Graphql.Document.decoder Subscription.tellMeSeconds)
        |> Result.toMaybe



-- Subscriptions


subscriptions : Model -> Sub Msg
subscriptions _ =
    secondsReceived SecondsResponseReceived



-- View


view : Model -> Html Msg
view model =
    div []
        [ showPosition model.position
        , button [ onClick GetPositionClicked ] [ text "Get the position!" ]
        , button [ onClick TellMeSeconds ] [ text "plz tell me seconds" ]
        , showSeconds model.seconds
        ]


showSeconds : Maybe Int -> Html msg
showSeconds seconds =
    case seconds of
        Just s ->
            p [] [ text <| "Seconds: " ++ String.fromInt s ]

        Nothing ->
            text ""


showPosition : Maybe Position -> Html msg
showPosition position_ =
    case position_ of
        Just { x, y } ->
            p [] [ text <| "Got the position! (" ++ String.fromInt x ++ ", " ++ String.fromInt y ++ ")" ]

        Nothing ->
            p [] [ text "No position yet" ]
