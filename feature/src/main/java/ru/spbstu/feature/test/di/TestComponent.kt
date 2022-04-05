package ru.spbstu.feature.test.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.test.presentation.TestFragment

@Subcomponent(
    modules = [
        TestModule::class
    ]
)
@ScreenScope
interface TestComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): TestComponent
    }

    fun inject(testFragment: TestFragment)
}
