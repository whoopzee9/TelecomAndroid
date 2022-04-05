package ru.spbstu.feature.test.presentation

import android.util.Log
import androidx.core.net.toUri
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.error.ErrorEntity
import ru.spbstu.common.error.TelecomResult
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.FilesModel
import ru.spbstu.feature.domain.usecase.GetFilesNamesUseCase
import ru.spbstu.feature.domain.usecase.UploadFileUseCase
import timber.log.Timber
import java.io.File

class TestViewModel(
    val router: FeatureRouter,
    private val getFilesNamesUseCase: GetFilesNamesUseCase,
    private val uploadFileUseCase: UploadFileUseCase
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

    fun getFilesNames() {
        _state.value = ResponseState.Processing
        getFilesNamesUseCase.invoke()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is TelecomResult.Success -> {
                        _filesNames.value = it.data.mapIndexed { index, s -> FilesModel(index.toLong(), s) }
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
        _state.value = ResponseState.Processing
        _filesUploadState.value = FileUploadState.Processing
        currentFile = filesList.indexOf(file) + 1
        uploadFileUseCase.invoke(file, callback)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    is TelecomResult.Success -> {
                        getFilesNames()
                        _filesUploadState.value = FileUploadState.FileLoadedSuccess
                        _state.value = ResponseState.Success
                        uploadNextPhotoIfExist(file, callback)
                    }
                    is TelecomResult.Error -> {
                        _state.value = ResponseState.Failure(it.error)
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
        } else {
            uploadFile(filesList[index + 1], callback)
        }
    }

    fun onItemClick(id: Long) {

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
