package ru.spbstu.common.extenstions

import android.webkit.MimeTypeMap
import java.io.File


fun File.getMimeType(): String {
    var type: String? = null
    val extension = MimeTypeMap.getFileExtensionFromUrl(this.absolutePath)
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    return type ?: ""
}