package com.ipv.farmmonitor

import android.app.Activity
import android.os.Build
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat



class Utils {

    companion object {
        @JvmStatic  fun fullScreen(activity: Activity) {

            WindowCompat.setDecorFitsSystemWindows(activity.window, false)
            val windowInsetsCompat = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
            windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars())
            windowInsetsCompat.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (activity.window.insetsController != null) {
                    val insetsController = activity.window.insetsController
                    if (insetsController != null) {
                        insetsController.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                        insetsController.systemBarsBehavior =
                            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                }
            } else {
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
            }
        }
    }


}