package ru.spbstu.common.extenstions

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

fun Fragment.statusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
    else 0
}

fun Fragment.handleBackPressed(handler: () -> Unit) {
    requireActivity().onBackPressedDispatcher.addCallback(
        viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handler.invoke()
            }
        }
    )
}

fun Fragment.handleBackPressed(callback: OnBackPressedCallback) {
    requireActivity().onBackPressedDispatcher.addCallback(
        viewLifecycleOwner,
        callback
    )
}

fun Fragment.tag(): String = this::class.java.simpleName
