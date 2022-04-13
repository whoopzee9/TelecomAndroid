package ru.spbstu.feature.domain.usecase

import android.content.Context
import net.gotev.uploadservice.observer.request.RequestObserver
import ru.spbstu.feature.domain.repository.FeatureRepository
import java.io.File
import javax.inject.Inject

class UploadFileInServiceUseCase @Inject constructor(
    private val featureRepository: FeatureRepository
) {
    operator fun invoke(file: File, context: Context, requestObserver: RequestObserver) {
        featureRepository.uploadFilesInService(file, context, requestObserver)
    }
}