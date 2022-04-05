package ru.spbstu.feature.domain.model

import ru.spbstu.common.base.BaseModel

data class FilesModel(
    override val id: Long = 0,
    val name: String = ""
) : BaseModel(id) {

    override fun isContentEqual(other: BaseModel): Boolean =
        other is FilesModel && name == other.name
}
