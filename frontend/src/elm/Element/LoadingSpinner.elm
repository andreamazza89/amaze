module Element.LoadingSpinner exposing (spinner)

import Element exposing (Element)
import Html
import Html.Attributes


spinner : Element msg
spinner =
    let
        border =
            "2px solid "

        borderSolid =
            border ++ "black"

        gap =
            border ++ "transparent"
    in
    Element.html <|
        Html.div
            [ Html.Attributes.style "animation" "spin 0.7s linear infinite"
            , Html.Attributes.style "border-radius" "50%"
            , Html.Attributes.style "border-top" borderSolid
            , Html.Attributes.style "border-right" borderSolid
            , Html.Attributes.style "border-bottom" borderSolid
            , Html.Attributes.style "border-left" gap
            , Html.Attributes.style "width" "10px"
            , Html.Attributes.style "height" "10px"
            ]
            []
