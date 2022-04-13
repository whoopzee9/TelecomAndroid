package ru.spbstu.feature.auth.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.auth.presentation.AuthFragment

@Subcomponent(
    modules = [
        AuthModule::class
    ]
)
@ScreenScope
interface AuthComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): AuthComponent
    }

    fun inject(authFragment: AuthFragment)
}