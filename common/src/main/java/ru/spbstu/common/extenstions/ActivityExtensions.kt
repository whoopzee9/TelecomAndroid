package ru.spbstu.common.extenstions

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.annotation.ColorRes

@Suppress("DEPRECATION")
fun Activity.setStatusBarColor(@ColorRes color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.statusBarColor = resources.getColor(color, theme)
    } else {
        window.statusBarColor = resources.getColor(color)
    }
}

fun Activity.setTransparentBar() {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
}

fun Activity.clearTransparentBar() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
}
