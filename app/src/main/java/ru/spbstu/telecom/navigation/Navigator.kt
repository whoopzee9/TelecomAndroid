package ru.spbstu.telecom.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import org.greenrobot.eventbus.EventBus
import ru.spbstu.common.events.SetBottomNavVisibility
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.telecom.R

class Navigator : FeatureRouter {

    private var navController: NavController? = null
    private var activity: AppCompatActivity? = null

    override fun back() {
        val popped = navController!!.popBackStack()

        if (!popped) {
            activity!!.finish()
        }
    }

    fun attachActivity(activity: AppCompatActivity) {
        this.activity = activity
    }

    fun attachNavController(navController: NavController) {
        this.navController = navController.apply {
            this.addOnDestinationChangedListener { _: NavController, _: NavDestination, _: Bundle? ->
                checkBottomBar()
            }
        }
    }

    fun detachActivity() {
        activity = null
    }

    fun detachNavController() {
        navController = null
    }

    override fun openMainFragment() {
        when (navController?.currentDestination?.id) {
            R.id.authFragment -> navController?.navigate(R.id.action_authFragment_to_testFragment)
        }
    }

    fun checkBottomBar() {
        EventBus.getDefault().post(
            SetBottomNavVisibility(
                !navBarHiddenIdsList.contains(navController?.currentDestination?.id),
                true
            )
        )
    }

    private companion object {
        val navBarHiddenIdsList = listOf<Int>()
    }
}
