package ru.spbstu.common.extenstions

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.TextView
import androidx.annotation.ColorRes
import ru.spbstu.common.utils.DebounceClickListener
import ru.spbstu.common.utils.DebouncePostHandler

@Suppress("DEPRECATION")
fun View.setLightStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            var flags = systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            systemUiVisibility = flags
        }
    }
}

@Suppress("DEPRECATION")
fun View.clearLightStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowInsetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            var flags = systemUiVisibility
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            systemUiVisibility = flags
        }
    }
}

fun View.setDebounceClickListener(
    delay: Long = DebouncePostHandler.DEFAULT_DELAY,
    onClickListener: View.OnClickListener
) {
    setOnClickListener(DebounceClickListener(delay, onClickListener))
}

@SuppressLint("ResourceType")
fun TextView.setResourceColor(@ColorRes resId: Int) {
    val color = Color.parseColor(resources.getString(resId))
    this.setTextColor(color)
}

fun View.rotateView(startAngle: Float = 0f, endAngle: Float = 180f, duration: Long = 300) {
    val rotate = ObjectAnimator.ofFloat(this, "rotation", startAngle, endAngle)
    rotate.duration = duration
    rotate.start()
}

fun View.margin(
    left: Float? = null,
    top: Float? = null,
    right: Float? = null,
    bottom: Float? = null
) {
    layoutParams<ViewGroup.MarginLayoutParams> {
        left?.run { leftMargin = dpToPx(this).toInt() }
        top?.run { topMargin = dpToPx(this).toInt() }
        right?.run { rightMargin = dpToPx(this).toInt() }
        bottom?.run { bottomMargin = dpToPx(this).toInt() }
    }
}

inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
    if (layoutParams is T) block(layoutParams as T)
}

fun View.dpToPx(dp: Float): Float = context.dpToPx(dp)
