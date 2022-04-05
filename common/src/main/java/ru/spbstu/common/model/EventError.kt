package ru.spbstu.common.model

import androidx.annotation.StringRes
import ru.spbstu.common.R
import ru.spbstu.common.error.ErrorEntity

sealed class EventError(@StringRes val errResId: Int) : ErrorEntity {

    object ConnectionError : EventError(R.string.error_connection)
    object UnknownError : EventError(R.string.error_unknown)
}
