package ru.spbstu.telecom.root.presentation

import androidx.navigation.fragment.NavHostFragment
import ru.spbstu.common.base.BaseActivity
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.telecom.R
import ru.spbstu.telecom.databinding.ActivityRootBinding
import ru.spbstu.telecom.navigation.Navigator
import ru.spbstu.telecom.root.di.RootApi
import ru.spbstu.telecom.root.di.RootComponent
import javax.inject.Inject


class RootActivity : BaseActivity<RootViewModel>() {

    @Inject
    lateinit var navigator: Navigator

    override val binding: ActivityRootBinding by viewBinding(ActivityRootBinding::inflate)

    override fun setupViews() {
        super.setupViews()
        navigator.attachActivity(this)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        val navController = navHostFragment.navController
        navigator.attachNavController(navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        navigator.detachActivity()
        navigator.detachNavController()
    }

    override fun inject() {
        FeatureUtils.getFeature<RootComponent>(this, RootApi::class.java)
            .mainActivityComponentFactory()
            .create(this)
            .inject(this)
    }
}
