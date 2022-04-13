package ru.spbstu.common.error

sealed class TelecomResult<T> {
    data class Success<T>(val data: T) : TelecomResult<T>()
    data class Error<T>(val error: ErrorEntity) : TelecomResult<T>()
}