package ru.spbstu.common.token

interface TokenRepository {
    fun getToken(): String?
    fun saveToken(token: String)
    fun getRefresh(): RefreshToken?
    fun saveRefresh(refresh: String)
    fun saveDownloadId(fileName: String, downloadId: Long)
    fun getDownloadId(fileName: String): Long
    fun saveFileOriginalPath(fileName: String, uriStr: String)
    fun getFileOriginalPath(fileName: String): String
    fun setAuthCode(code: String)
    fun getAuthCode(): String
}
