package ru.spbstu.common.utils

import ru.spbstu.common.base.BaseBackRouter
import ru.spbstu.common.base.BaseViewModel

open class BackViewModel(private val router: BaseBackRouter) : BaseViewModel() {

    fun back() {
        router.back()
    }
}
