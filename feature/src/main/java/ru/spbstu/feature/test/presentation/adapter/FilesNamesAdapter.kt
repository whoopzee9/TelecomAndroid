package ru.spbstu.feature.test.presentation.adapter

import android.view.View
import android.view.ViewGroup
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.databinding.ItemTestBinding
import ru.spbstu.feature.domain.model.FilesModel


class FilesNamesAdapter(val onItemClick: (String) -> Unit) :
    BaseAdapter<FilesModel, FilesNamesAdapter.TestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder =
        TestViewHolder(parent)

    inner class TestViewHolder(parent: ViewGroup) :
        BaseViewHolder<FilesModel, ItemTestBinding>(parent.viewBinding(ItemTestBinding::inflate)) {

        private lateinit var item: FilesModel

        init {
            binding.root.setDebounceClickListener {
                onItemClick(item.name)
            }
        }

        override fun bind(item: FilesModel) {
            this.item = item
            binding.itemTestTvText.text = item.name
            binding.itemTestIvDownload.visibility = if (item.isExist) View.GONE else View.VISIBLE
            binding.itemTestPbProgress.visibility = if (item.isLoading) View.VISIBLE else View.GONE
        }
    }
}
