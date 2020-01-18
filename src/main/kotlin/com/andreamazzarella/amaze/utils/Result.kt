package com.andreamazzarella.amaze.utils

import java.lang.RuntimeException

sealed class Result<O, E>
data class Ok<O, E>(val okValue: O) : Result<O, E>()
data class Err<O, E>(val errorValue: E) : Result<O, E>()

fun <O, E, T> Result<O, E>.map(doThisIfAllWentWell: (O) -> T): Result<T, E> {
    return when (this) {
        is Ok -> Ok(doThisIfAllWentWell(this.okValue))
        is Err -> Err(this.errorValue)
    }
}

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

// fun <O, E, T> Result<O, E>.whenError(f: (E) -> T): Result<O,T> {
//     return when (this) {
//         is Ok -> doThisIfAllWentWell(this.okValue)
//         is Err -> Err(this.errorValue)
//     }
// }

fun <O, E> Result<O, E>.okOrFail(): O {
    return when (this) {
        is Ok -> this.okValue
        is Err -> throw RuntimeException("value should not be an error when calling this function")
    }
}

fun <O, E> Result<O, E>.runOnOk(run: (O) -> Unit): Result<O, E> {
    return when (this) {
        is Ok -> {
            run(this.okValue)
            this
        }
        is Err -> Err(this.errorValue)
    }
}
