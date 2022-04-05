package ru.spbstu.feature.data.remote.api

import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FeatureApiService {
    @GET("/file/all")
    fun getFilesNames(): Single<Response<List<String>>>

    @Multipart
    @POST("/file/upload")
    fun uploadImage(@Part files: MultipartBody.Part): Single<Response<List<String>>>
}
