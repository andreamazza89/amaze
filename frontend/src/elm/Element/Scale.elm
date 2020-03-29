module Element.Scale exposing
    ( extraLarge
    , extraSmall
    , large
    , medium
    , small
    , superExtraLarge
    )


superExtraLarge : Int
superExtraLarge =
    atom * 10


extraLarge : Int
extraLarge =
    atom * 5


large : Int
large =
    atom * 4


medium : Int
medium =
    atom * 3


small : Int
small =
    atom * 2


extraSmall : Int
extraSmall =
    atom


atom : Int
atom =
    5
