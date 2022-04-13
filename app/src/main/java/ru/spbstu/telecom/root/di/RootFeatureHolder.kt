package ru.spbstu.telecom.root.di

import ru.spbstu.common.di.FeatureApiHolder
import ru.spbstu.common.di.FeatureContainer
import ru.spbstu.common.di.scope.ApplicationScope
import ru.spbstu.telecom.navigation.Navigator
import javax.inject.Inject

@ApplicationScope
class RootFeatureHolder @Inject constructor(
    featureContainer: FeatureContainer,
    private val navigator: Navigator
) : FeatureApiHolder(featureContainer) {

    override fun initializeDependencies(): Any {
        val rootFeatureDependencies = DaggerRootComponent_RootFeatureDependenciesComponent.builder()
            .commonApi(commonApi())
            .build()
        return DaggerRootComponent.factory()
            .create(navigator, rootFeatureDependencies)
    }
}
