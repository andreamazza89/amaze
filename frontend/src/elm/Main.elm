port module Main exposing (main)

import Api.Object
import Api.Object.MazeInfo
import Api.Object.Position
import Api.Subscription as Subscription
import Browser
import Graphql.Document
import Graphql.Operation exposing (RootSubscription)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet(..), with)
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
    { mazes : List MazeInfoInternal
    }


type Msg
    = MazesInformationReceived Decode.Value
    | SubscribeToMazeUpdates


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
        [ button [ onClick SubscribeToMazeUpdates ] [ text "subscribe to maze updates" ]
        , div [] <| List.map viewMazeInfo model.mazes
        ]


viewMazeInfo : MazeInfoInternal -> Html msg
viewMazeInfo info =
    p [] [ text <| "this is one maze; current x: " ++ String.fromInt info.position.x ++ ", y: " ++ String.fromInt info.position.y ]
