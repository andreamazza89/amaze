module Utils.List exposing (chunk)


chunk : Int -> List a -> List (List a)
chunk k xs =
    let
        len =
            List.length xs
    in
    if len > k then
        List.take k xs :: chunk k (List.drop k xs)

    else
        [ xs ]
