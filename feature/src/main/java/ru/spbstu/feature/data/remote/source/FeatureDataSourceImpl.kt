package ru.spbstu.feature.data.remote.source

import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.spbstu.common.error.TelecomResult
import ru.spbstu.common.utils.UploadRequestBody
import ru.spbstu.feature.data.remote.api.FeatureApiService
import ru.spbstu.feature.data.remote.model.ResponseError
import ru.spbstu.feature.data.source.FeatureDataSource
import java.io.File
import javax.inject.Inject

class FeatureDataSourceImpl @Inject constructor(private val featureApiService: FeatureApiService) :
    FeatureDataSource {
    override fun getFilesNames(): Single<TelecomResult<List<String>>> {
        return featureApiService.getFilesNames().map {
            when {
                it.isSuccessful -> {
                    val res = it.body()
                    if (res != null) {
                        TelecomResult.Success(res)
                    } else {
                        TelecomResult.Error(ResponseError.UnknownError)
                    }
                }
                else -> {
                    TelecomResult.Error(ResponseError.UnknownError)
                }
            }
        }
    }

    override fun uploadFiles(files: File, callback: (Int, Long, Long) -> Unit): Single<TelecomResult<List<String>>> {
        val body = UploadRequestBody(files, callback)
        return featureApiService.uploadImage(
            MultipartBody.Part.createFormData(
            "files",
            files.name,
            body
        )).map {
            when {
                it.isSuccessful -> {
                    val res = it.body()
                    if (res != null) {
                        TelecomResult.Success(res)
                    } else {
                        TelecomResult.Error(ResponseError.UnknownError)
                    }
                }
                else -> {
                    TelecomResult.Error(ResponseError.UnknownError)
                }
            }
        }
    }
}
