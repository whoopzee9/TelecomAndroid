package ru.spbstu.feature.data.source

import io.reactivex.Single
import ru.spbstu.common.error.TelecomResult
import java.io.File

interface FeatureDataSource {
    fun getFilesNames(): Single<TelecomResult<List<String>>>
    fun uploadFiles(files: File, callback: (Int, Long, Long) -> Unit): Single<TelecomResult<List<String>>>
}
