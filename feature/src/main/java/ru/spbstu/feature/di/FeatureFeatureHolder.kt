package ru.spbstu.feature.di

import ru.spbstu.common.di.FeatureApiHolder
import ru.spbstu.common.di.FeatureContainer
import ru.spbstu.common.di.scope.ApplicationScope
import ru.spbstu.feature.FeatureRouter
import javax.inject.Inject

@ApplicationScope
class FeatureFeatureHolder @Inject constructor(
    featureContainer: FeatureContainer,
    private val featureRouter: FeatureRouter
) : FeatureApiHolder(featureContainer) {

    override fun initializeDependencies(): Any {
        val deps = DaggerFeatureComponent_FeatureDependenciesComponent.builder()
            .commonApi(commonApi())
            .build()
        return DaggerFeatureComponent.factory()
            .create(featureRouter, deps)
    }
}
