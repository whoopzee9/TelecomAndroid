package ru.spbstu.common.utils

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.databinding.IncludeToolbarBinding
import ru.spbstu.common.extenstions.handleBackPressed
import ru.spbstu.common.extenstions.setDebounceClickListener


abstract class ToolbarFragment<T : BackViewModel> constructor(
    @LayoutRes contentLayoutId: Int,
    @StringRes private val titleResource: Int = 0,
    private val type: ToolbarType = ToolbarType.BACK
) : BaseFragment<T>(contentLayoutId) {

    private var _layoutToolbarBinding: IncludeToolbarBinding? = null
    private val layoutToolbarBinding get() = _layoutToolbarBinding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
    }

    override fun onDestroy() {
        _layoutToolbarBinding = null
        super.onDestroy()
    }

    protected fun setToolbar(clickListener: () -> Unit) {
        setToolbar(clickListener = clickListener)
    }

    protected abstract fun getToolbarLayout(): ViewGroup

    private fun setToolbar(
        type: ToolbarType = this.type,
        @StringRes titleResource: Int = this.titleResource,
        clickListener: (() -> Unit)? = { viewModel.back() }
    ) {
        _layoutToolbarBinding = IncludeToolbarBinding.bind(getToolbarLayout())
        when (type) {
            ToolbarType.EMPTY -> {
                val params = layoutToolbarBinding.includeToolbarIbButton.layoutParams
                params.width = 0
                params.height = 0
                layoutToolbarBinding.includeToolbarIbButton.layoutParams = params
            }
            else -> {
                layoutToolbarBinding.includeToolbarIbButton.setImageResource(type.icon)
                layoutToolbarBinding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.transparent
                    )
                )
            }
        }
        if (titleResource != 0) {
            layoutToolbarBinding.includeToolbarTvTitle.text = getString(titleResource)
        }
        clickListener?.let {
            layoutToolbarBinding.includeToolbarIbButton.setDebounceClickListener {
                it()
            }
            handleBackPressed {
                it()
            }
        }
    }

    enum class ToolbarType(@DrawableRes val icon: Int) {
        BACK(android.R.drawable.ic_delete), EMPTY(0)
    }
}
