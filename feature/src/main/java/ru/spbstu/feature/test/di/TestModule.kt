package ru.spbstu.feature.test.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.common.token.TokenRepository
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.usecase.GetFilesNamesUseCase
import ru.spbstu.feature.domain.usecase.UploadFileInServiceUseCase
import ru.spbstu.feature.domain.usecase.UploadFileUseCase
import ru.spbstu.feature.test.presentation.TestViewModel

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class TestModule {

    @Provides
    @IntoMap
    @ViewModelKey(TestViewModel::class)
    fun provideViewModel(
        router: FeatureRouter,
        getFilesNamesUseCase: GetFilesNamesUseCase,
        uploadFileUseCase: UploadFileUseCase,
        uploadFilesInServiceUseCase: UploadFileInServiceUseCase,
        tokenRepository: TokenRepository
    ): ViewModel {
        return TestViewModel(
            router,
            getFilesNamesUseCase,
            uploadFileUseCase,
            uploadFilesInServiceUseCase,
            tokenRepository
        )
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): TestViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(TestViewModel::class.java)
    }
}
