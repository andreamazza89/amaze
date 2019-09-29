package com.andreamazzarella.amaze.utils

sealed class Result<O, E>
data class Ok<O, E>(val okValue: O) : Result<O, E>()
data class Err<O, E>(val errorValue: E) : Result<O, E>()

fun <O, E, OtherErrorType> Result<O, E>.mapError(toError: (E) -> OtherErrorType): Result<O, OtherErrorType> {
    return when (this) {
        is Ok -> Ok(this.okValue)
        is Err -> Err(toError(this.errorValue))
    }
}

fun <O, E, T> Result<O, E>.andThen(doThisIfAllWentWell: (O) -> Result<T, E>): Result<T, E> {
    return when (this) {
        is Ok -> doThisIfAllWentWell(this.okValue)
        is Err -> Err(this.errorValue)
    }
}
