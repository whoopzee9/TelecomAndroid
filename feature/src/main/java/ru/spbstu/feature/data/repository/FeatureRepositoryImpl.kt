package ru.spbstu.feature.data.repository

import io.reactivex.Single
import ru.spbstu.common.error.TelecomResult
import ru.spbstu.feature.data.source.FeatureDataSource
import ru.spbstu.feature.domain.repository.FeatureRepository
import java.io.File
import javax.inject.Inject

class FeatureRepositoryImpl @Inject constructor(private val featureDataSource: FeatureDataSource) :
    FeatureRepository {
    override fun getFilesNames(): Single<TelecomResult<List<String>>> {
        return featureDataSource.getFilesNames()
    }

    override fun uploadFiles(files: File, callback: (Int, Long, Long) -> Unit): Single<TelecomResult<List<String>>> {
        return featureDataSource.uploadFiles(files, callback)
    }
}
