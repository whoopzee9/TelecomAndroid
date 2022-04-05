package ru.spbstu.feature.domain.repository

import io.reactivex.Single
import ru.spbstu.common.error.TelecomResult
import java.io.File

interface FeatureRepository {
    fun getFilesNames(): Single<TelecomResult<List<String>>>
    fun uploadFiles(files: File, callback: (Int, Long, Long) -> Unit): Single<TelecomResult<List<String>>>
}
