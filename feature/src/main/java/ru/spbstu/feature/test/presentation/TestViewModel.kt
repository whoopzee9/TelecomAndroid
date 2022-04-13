package ru.spbstu.feature.test.presentation

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.gotev.uploadservice.data.UploadInfo
import net.gotev.uploadservice.network.ServerResponse
import net.gotev.uploadservice.observer.request.RequestObserver
import net.gotev.uploadservice.observer.request.RequestObserverDelegate
import ru.spbstu.common.error.ErrorEntity
import ru.spbstu.common.error.TelecomResult
import ru.spbstu.common.token.TokenRepository
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.data.remote.model.ResponseError
import ru.spbstu.feature.domain.model.FilesModel
import ru.spbstu.feature.domain.usecase.GetFilesNamesUseCase
import ru.spbstu.feature.domain.usecase.UploadFileInServiceUseCase
import ru.spbstu.feature.domain.usecase.UploadFileUseCase
import timber.log.Timber
import java.io.File

class TestViewModel(
    val router: FeatureRouter,
    private val getFilesNamesUseCase: GetFilesNamesUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val uploadFilesInServiceUseCase: UploadFileInServiceUseCase,
    private val tokenRepository: TokenRepository
) : BackViewModel(router) {

    private val _filesNames: MutableStateFlow<List<FilesModel>> = MutableStateFlow(listOf())
    val filesNames get() :StateFlow<List<FilesModel>> = _filesNames

    private val _state: MutableStateFlow<ResponseState> = MutableStateFlow(ResponseState.Initial)
    val state: StateFlow<ResponseState> get() = _state

    private val _filesUploadState: MutableStateFlow<FileUploadState> =
        MutableStateFlow(FileUploadState.Initial)
    val filesUploadState: StateFlow<FileUploadState> get() = _filesUploadState

    var filesList = mutableListOf<File>()

    var currentFile = 0

    fun getFilesNames(dirFile: File?) {
        _state.value = ResponseState.Processing
        getFilesNamesUseCase.invoke()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is TelecomResult.Success -> {
                        val namesList = mutableListOf<String>()
                        dirFile?.listFiles()?.forEach { file ->
                            namesList.add(file.name)
                        }
                        _filesNames.value =
                            it.data.mapIndexed { index, s ->
                                val orig = File(tokenRepository.getFileOriginalPath(s))
                                FilesModel(
                                    index.toLong(),
                                    s,
                                    false,
                                    namesList.contains(s) || orig.exists()
                                )
                            }
                        _state.value = ResponseState.Success
                    }
                    is TelecomResult.Error -> {
                        _state.value = ResponseState.Failure(it.error)
                        Timber.tag(TAG).i(it.error.toString())
                    }
                }
            }, {
                it.printStackTrace()
            })
            .addTo(disposable)
    }

    fun uploadFile(file: File, callback: (Int, Long, Long) -> Unit) {
        _filesUploadState.value = FileUploadState.Processing
        currentFile = filesList.indexOf(file) + 1
        uploadFileUseCase.invoke(file, callback)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is TelecomResult.Success -> {
                        _filesUploadState.value = FileUploadState.FileLoadedSuccess
                        uploadNextPhotoIfExist(file, callback)
                    }
                    is TelecomResult.Error -> {
                        _filesUploadState.value = FileUploadState.Failure(it.error)
                        Timber.tag(TAG).i(it.error.toString())
                    }
                }
            }, {
                it.printStackTrace()
            })
            .addTo(disposable)
    }

    private fun uploadNextPhotoIfExist(file: File, callback: (Int, Long, Long) -> Unit) {
        val index = filesList.indexOf(file)
        if (index + 1 >= filesList.size) {
            _filesUploadState.value = FileUploadState.FileLoadedSuccess
            //getFilesNames()
        } else {
            uploadFile(filesList[index + 1], callback)
        }
    }

    fun uploadFileInService(
        file: File,
        context: Context,
        lifecycleOwner: LifecycleOwner,
        callback: (Int, Long, Long) -> Unit
    ) {
        _filesUploadState.value = FileUploadState.Processing
        currentFile = filesList.indexOf(file) + 1
        uploadFilesInServiceUseCase.invoke(
            file,
            context,
            RequestObserver(context, lifecycleOwner, object : RequestObserverDelegate {
                override fun onCompleted(context: Context, uploadInfo: UploadInfo) {
                }

                override fun onCompletedWhileNotObserving() {
                }

                override fun onError(
                    context: Context,
                    uploadInfo: UploadInfo,
                    exception: Throwable
                ) {
                    Timber.tag(TAG).e(exception)
                    _filesUploadState.value = FileUploadState.Failure(ResponseError.UnknownError)
                }

                override fun onProgress(context: Context, uploadInfo: UploadInfo) {
                    callback.invoke(
                        uploadInfo.progressPercent,
                        uploadInfo.uploadedBytes,
                        uploadInfo.totalBytes
                    )
                }

                override fun onSuccess(
                    context: Context,
                    uploadInfo: UploadInfo,
                    serverResponse: ServerResponse
                ) {
                    tokenRepository.saveFileOriginalPath(file.name, file.absolutePath)
                    _filesUploadState.value = FileUploadState.FileLoadedSuccess
                }

            })
        )
    }

    fun onItemClick(id: Long) {

    }

    fun saveDownloadId(fileName: String, id: Long) {
        tokenRepository.saveDownloadId(fileName, id)
    }

    fun getDownloadId(fileName: String): Long {
        return tokenRepository.getDownloadId(fileName)
    }

    fun getOriginalFilePath(fileName: String): String {
        return tokenRepository.getFileOriginalPath(fileName)
    }

    fun setStates(fileName: String, isLoading: Boolean, isExist: Boolean) {
        _filesNames.value = filesNames.value.map {
            if (it.name == fileName) {
                it.copy(isLoading = isLoading, isExist = isExist)
            } else {
                it
            }
        }
    }

    sealed class FileUploadState {
        object Initial : FileUploadState()
        object FileLoadedSuccess : FileUploadState()
        object Processing : FileUploadState()
        data class Failure(val error: ErrorEntity) : FileUploadState()
    }

    sealed class ResponseState {
        object Initial : ResponseState()
        object Processing : ResponseState()
        object Success : ResponseState()
        data class Failure(val error: ErrorEntity) : ResponseState()
    }

    private companion object {
        const val TEST_RANDOM_SIZE = 33
        const val TEST_PREFIX = "Test"
        var TAG: String = TestViewModel::class.java.simpleName

    }
}
