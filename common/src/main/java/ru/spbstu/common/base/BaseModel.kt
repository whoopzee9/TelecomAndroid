package ru.spbstu.common.base

abstract class BaseModel(open val id: Long) {

    open fun isModelEqual(other: BaseModel): Boolean = id == other.id
    abstract fun isContentEqual(other: BaseModel): Boolean
}
