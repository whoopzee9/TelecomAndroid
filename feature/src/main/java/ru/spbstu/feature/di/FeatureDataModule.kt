package ru.spbstu.feature.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import ru.spbstu.common.di.scope.FeatureScope
import ru.spbstu.feature.data.remote.api.FeatureApiService
import ru.spbstu.feature.data.remote.source.FeatureDataSourceImpl
import ru.spbstu.feature.data.repository.FeatureRepositoryImpl
import ru.spbstu.feature.data.source.FeatureDataSource
import ru.spbstu.feature.domain.repository.FeatureRepository

@Module
abstract class FeatureDataModule {

    @Binds
    @FeatureScope
    abstract fun bindFeatureRepository(featureRepositoryImpl: FeatureRepositoryImpl): FeatureRepository

    @Binds
    @FeatureScope
    abstract fun bindFeatureDataSource(featureDataSourceImpl: FeatureDataSourceImpl): FeatureDataSource

    companion object {
        @Provides
        @FeatureScope
        fun provideFeatureApiService(retrofit: Retrofit): FeatureApiService =
            retrofit.create(FeatureApiService::class.java)
    }
}
