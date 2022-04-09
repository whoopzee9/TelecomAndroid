package ru.spbstu.feature.auth.di

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
import ru.spbstu.feature.auth.presentation.AuthViewModel

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class AuthModule {

    @Provides
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    fun provideViewModel(
        router: FeatureRouter,
        tokenRepository: TokenRepository
    ): ViewModel {
        return AuthViewModel(router, tokenRepository)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): AuthViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(AuthViewModel::class.java)
    }
}