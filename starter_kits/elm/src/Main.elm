module Main exposing (main)

import Browser
import Html exposing (Html, div, input, text)
import Html.Attributes exposing (placeholder, value)
import Html.Events exposing (onInput)


main : Program () { serverUrl : String } Msg
main =
    Browser.element { init = init, update = update, view = view, subscriptions = always Sub.none }


type alias Model =
    { serverUrl : String }


type Msg
    = ServerUrlTyped String


init _ =
    ( { serverUrl = "" }, Cmd.none )


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        ServerUrlTyped url ->
            ( { model | serverUrl = url }, Cmd.none )


view : Model -> Html Msg
view model =
    div []
        [ text "hi"
        , input
            [ onInput ServerUrlTyped
            , placeholder "type the server url here"
            , value model.serverUrl
            ]
            []
        ]
