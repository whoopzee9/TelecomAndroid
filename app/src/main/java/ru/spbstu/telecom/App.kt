package ru.spbstu.telecom

import android.app.Application
import androidx.viewbinding.BuildConfig
import ru.spbstu.common.di.CommonApi
import ru.spbstu.common.di.FeatureContainer
import ru.spbstu.telecom.di.app.AppComponent
import ru.spbstu.telecom.di.app.DaggerAppComponent
import ru.spbstu.telecom.di.deps.FeatureHolderManager
import ru.spbstu.telecom.log.ReleaseTree
import timber.log.Timber
import javax.inject.Inject

open class App: Application(), FeatureContainer {

    @Inject
    lateinit var featureHolderManager: FeatureHolderManager

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
                .builder()
                .application(this)
                .build()

        appComponent.inject(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    override fun <T> getFeature(key: Class<*>): T {
        return featureHolderManager.getFeature<T>(key)!!
    }

    override fun releaseFeature(key: Class<*>) {
        featureHolderManager.releaseFeature(key)
    }

    override fun commonApi(): CommonApi {
        return appComponent
    }
}
