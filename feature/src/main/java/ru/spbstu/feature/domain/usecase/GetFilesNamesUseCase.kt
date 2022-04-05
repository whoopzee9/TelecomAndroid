package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import ru.spbstu.common.error.TelecomResult
import ru.spbstu.feature.domain.repository.FeatureRepository
import javax.inject.Inject

class GetFilesNamesUseCase @Inject constructor(
    private val featureRepository: FeatureRepository
) {
    operator fun invoke(): Single<TelecomResult<List<String>>> {
        return featureRepository.getFilesNames()
    }
}