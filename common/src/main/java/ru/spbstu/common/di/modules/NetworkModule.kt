package ru.spbstu.common.di.modules

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.spbstu.common.BuildConfig
import ru.spbstu.common.di.interceptors.TokenInterceptor
import ru.spbstu.common.di.scope.ApplicationScope
import ru.spbstu.common.token.TokenRepository
import java.util.concurrent.TimeUnit

@Module
class NetworkModule {

    @Provides
    @ApplicationScope
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @ApplicationScope
    internal fun provideRestInterceptor(
        gson: Gson,
        tokenRepository: TokenRepository
    ): Interceptor = TokenInterceptor(gson, tokenRepository)

    @Provides
    @ApplicationScope
    internal fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        return interceptor
    }

    @Provides
    @ApplicationScope
    fun provideOkHttpClient(
        restInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(restInterceptor)
            .addInterceptor(loggingInterceptor)
        return builder.build()
    }

    @Provides
    @ApplicationScope
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.ENDPOINT)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
}
