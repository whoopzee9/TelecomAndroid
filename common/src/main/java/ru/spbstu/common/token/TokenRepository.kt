package ru.spbstu.common.token

interface TokenRepository {
    fun getToken(): String?
    fun saveToken(token: String)
    fun getRefresh(): RefreshToken?
    fun saveRefresh(refresh: String)
    fun saveDownloadId(fileName: String, downloadId: Long)
    fun getDownloadId(fileName: String): Long
}
