package ru.spbstu.telecom.di.deps

import dagger.Binds
import dagger.Module
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.FeatureApiHolder
import ru.spbstu.common.di.FeatureContainer
import ru.spbstu.common.di.scope.ApplicationScope
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureFeatureHolder
import ru.spbstu.telecom.App
import ru.spbstu.telecom.root.di.RootApi
import ru.spbstu.telecom.root.di.RootFeatureHolder

@Module
interface ComponentHolderModule {

    @ApplicationScope
    @Binds
    fun provideFeatureContainer(application: App): FeatureContainer

    @ApplicationScope
    @Binds
    @ClassKey(RootApi::class)
    @IntoMap
    fun provideMainFeature(rootFeatureHolder: RootFeatureHolder): FeatureApiHolder

    @ApplicationScope
    @Binds
    @ClassKey(FeatureApi::class)
    @IntoMap
    fun provideFeatureFeature(featureHolder: FeatureFeatureHolder): FeatureApiHolder
}
