package ru.spbstu.telecom.di.app

import dagger.Module
import dagger.Provides
import ru.spbstu.common.di.FeatureApiHolder
import ru.spbstu.common.di.scope.ApplicationScope
import ru.spbstu.telecom.di.deps.FeatureHolderManager

@Module
class FeatureManagerModule {

    @ApplicationScope
    @Provides
    fun provideFeatureHolderManager(featureApiHolderMap: @JvmSuppressWildcards Map<Class<*>, FeatureApiHolder>): FeatureHolderManager {
        return FeatureHolderManager(featureApiHolderMap)
    }
}
