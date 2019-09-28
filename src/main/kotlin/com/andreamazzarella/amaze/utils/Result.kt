package com.andreamazzarella.amaze.utils

sealed class Result<O, E> {
    data class Ok<O, E>(val okValue: O) : Result<O, E>()
    data class Error<O, E>(val errorValue: E) : Result<O, E>()

    fun <T> andThen(doThisIfAllWentWell: (O) -> Result<T, E>): Result<T, E> {
        return when (this) {
            is Ok -> doThisIfAllWentWell(this.okValue)
            is Error -> Error(this.errorValue)
        }
    }

    fun <OtherErrorType> mapError(toError: (E) -> OtherErrorType): Result<O, OtherErrorType> {
        return when (this) {
            is Ok -> Ok(this.okValue)
            is Error -> Error(toError(this.errorValue))
        }
    }
}
