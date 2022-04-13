package ru.spbstu.feature.data.remote.model

import androidx.annotation.StringRes
import ru.spbstu.common.error.ErrorEntity
import ru.spbstu.feature.R

sealed class ResponseError(@StringRes val errorResId: Int) : ErrorEntity {
    object UnknownError : ResponseError(R.string.unknown_error)
}