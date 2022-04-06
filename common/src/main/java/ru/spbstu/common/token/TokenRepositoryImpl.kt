package ru.spbstu.common.token

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Named

class TokenRepositoryImpl @Inject constructor(
    @Named("encrypted")
    private val sharedPreferences: SharedPreferences
) : TokenRepository {

    override fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    override fun saveToken(token: String) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
    }

    override fun getRefresh(): RefreshToken? {
        val token = sharedPreferences.getString(REFRESH_KEY, null)
        return if (token != null) RefreshToken(token) else null
    }

    override fun saveRefresh(refresh: String) {
        sharedPreferences.edit().putString(REFRESH_KEY, refresh).apply()
    }

    override fun saveDownloadId(fileName: String, downloadId: Long) {
        sharedPreferences.edit().putLong(fileName, downloadId).apply()
    }

    override fun getDownloadId(fileName: String): Long {
        return sharedPreferences.getLong(fileName, 0)
    }

    private companion object {
        private const val TOKEN_KEY = "ru.spbstu.telecom.TokenRepositoryImpl.token"
        private const val REFRESH_KEY = "ru.spbstu.telecom.TokenRepositoryImpl.refresh"
    }
}
