package ru.spbstu.common.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity<T : BaseViewModel> : AppCompatActivity() {

    @Inject
    protected open lateinit var viewModel: T

    protected abstract val binding: ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        inject()
        setupViews()
        subscribe()
    }

    protected open fun setupViews() {}

    protected open fun subscribe() {}

    protected abstract fun inject()

    protected inline fun <V> Flow<V>.observe(crossinline collector: suspend (V) -> Unit) {
        lifecycleScope.launch {
            this@observe.collect(collector)
        }
    }
}
