package ru.spbstu.common.di.interceptors

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.spbstu.common.BuildConfig
import ru.spbstu.common.di.modules.TokensResponse
import ru.spbstu.common.token.RefreshToken
import ru.spbstu.common.token.TokenRepository
import timber.log.Timber

class TokenInterceptor(private val gson: Gson, private val tokenRepository: TokenRepository) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val accessToken = tokenRepository.getToken()
        val requestBuilder = original.newBuilder()
        if (!accessToken.isNullOrEmpty() && !original.url.toString().contains("auth")) {
            requestBuilder.addHeader(AUTHORIZATION, BEARER + accessToken)
        }
        val request = requestBuilder.build()
        var response = chain.proceed(request)
        Timber.tag(TAG).i("Processed request(no tokens)=$original, response=$response")
        if (response.code == 401 && !original.url.toString().contains("auth")) {
            val refreshToken: RefreshToken? = tokenRepository.getRefresh()
            val authRequest = request.newBuilder()
                .post(gson.toJson(refreshToken).toRequestBody())
                .url(BuildConfig.REFRESH_ENDPOINT)
                .build()

            response.close()
            val refreshTokenResponse = chain.proceed(authRequest)
            if (refreshTokenResponse.code == 200) {
                val tokens =
                    gson.fromJson(refreshTokenResponse.body!!.string(), TokensResponse::class.java)
                tokenRepository.saveRefresh(tokens.refreshToken)
                tokenRepository.saveToken(tokens.accessToken)
                val currentRequest = original.newBuilder()
                    .addHeader(AUTHORIZATION, BEARER + tokens.accessToken)
                    .build()
                Timber.tag(TAG)
                    .d("NetworkModule: Tokens refreshed for $authRequest response=$refreshTokenResponse new_tokens=$tokens")
                refreshTokenResponse.close()
                response = chain.proceed(currentRequest)
            } else if (refreshTokenResponse.code == 401) {
                Timber.tag(TAG)
                    .d("NetworkModule: Refresh token died response=$refreshTokenResponse")
                //TODO: auth event
            }
        }
        return response
    }

    companion object {
        private val TAG = TokenInterceptor::class.java.simpleName
        private const val BEARER = "Bearer "
        private const val AUTHORIZATION = "Authorization"
    }
}
