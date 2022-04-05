package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import ru.spbstu.common.error.TelecomResult
import ru.spbstu.feature.domain.repository.FeatureRepository
import java.io.File
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(
    private val featureRepository: FeatureRepository
) {
    operator fun invoke(file: File, callback: (Int, Long, Long) -> Unit): Single<TelecomResult<List<String>>> {
        return featureRepository.uploadFiles(file, callback)
    }
}