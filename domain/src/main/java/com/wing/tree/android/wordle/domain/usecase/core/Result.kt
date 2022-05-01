package com.wing.tree.android.wordle.domain.usecase.core

sealed class Result<out R: Any> {
    data class Success<out T: Any>(val data: T) : Result<T>()
    data class Error(val throwable: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

inline fun <R: Any, T: Any> Result<T>.map(transform: (T) -> R): Result<R> {
    return when(this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> Result.Error(throwable)
        Result.Loading -> Result.Loading
    }
}

fun <T: Any> Result<T>.getOrDefault(defaultValue: T): T {
    return when(this) {
        Result.Loading -> defaultValue
        is Result.Error -> defaultValue
        is Result.Success -> data
    }
}