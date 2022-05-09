@file:Suppress("DEPRECATION")

package com.example.vitsi.utils

import android.app.Activity
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import com.example.vitsi.R
import com.example.vitsi.utils.ResUtils.getResColor

enum class SystemBarColors { WHITE, DARK }

fun View.hide() {
    visibility = View.GONE
}

object ViewUtils {

    @RequiresApi(M)
    private const val darkStatusIcons = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

    @RequiresApi(M)
    private const val whiteStatusIcons = 0

    fun changeSystemBars(activity: Activity?, systemBarColors: SystemBarColors) {
        activity?.let {
            setUpStatusBarAndNavigationBar(activity)

            if (systemBarColors == SystemBarColors.WHITE) {
                changeStatusBarIcons(activity, isWhiteIcons = false)
                changeStatusBarColor(activity, android.R.color.white)
                changeSystemNavigationBarColor(activity, android.R.color.white)
            } else {
                changeStatusBarIcons(activity, isWhiteIcons = true)
                changeStatusBarColor(activity, android.R.color.transparent)
                changeSystemNavigationBarColor(activity, R.color.dark_black)
            }
        }
    }

    private fun setUpStatusBarAndNavigationBar(activity: Activity) {
        with(activity) {
            if (Build.VERSION.SDK_INT >= M) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
        }
    }

    private fun changeStatusBarIcons(activity: Activity, isWhiteIcons: Boolean) {
        setUpStatusBarAndNavigationBar(activity)
        if (Build.VERSION.SDK_INT >= M) {
            activity.window?.decorView?.systemUiVisibility =
                if (isWhiteIcons) whiteStatusIcons else darkStatusIcons
        }
    }

    fun changeStatusBarColor(activity: Activity, @ColorRes colorRes: Int) {
        setUpStatusBarAndNavigationBar(activity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window?.statusBarColor = getResColor(activity.applicationContext, colorRes)
        }
    }

    fun changeSystemNavigationBarColor(activity: Activity, @ColorRes colorRes: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window?.navigationBarColor = getResColor(activity.applicationContext, colorRes)
        }
    }

    fun showStatusAndNavBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowInsetsController = activity.window.insetsController
            windowInsetsController?.show(WindowInsets.Type.statusBars())
            windowInsetsController?.show(WindowInsets.Type.systemBars())
        }
    }

    fun hideStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            activity.window.insetsController?.hide(WindowInsets.Type.statusBars())
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activity.window.decorView.systemUiVisibility =
                    (
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE // Prevents layout resize
                                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Initially load things behind the navigation bars. Prevents resize
                                    or View.SYSTEM_UI_FLAG_IMMERSIVE // Prevents the screen from showing the status bar, on tapping
                                    or View.SYSTEM_UI_FLAG_FULLSCREEN // Hide the status bar only
                            )
            }
        }
    }

    fun hideStatusAndNavBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowInsetsController = activity.window.insetsController
            windowInsetsController?.hide(WindowInsets.Type.statusBars())
            windowInsetsController?.hide(WindowInsets.Type.systemBars())
        } else {
            activity.window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_LAYOUT_STABLE // Prevents layout resize
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // Initially load things behind the navigation bar. Prevents resize
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Initially load things behind the navigation bars. Prevents resize
                        // Hide the nav bar and status bar
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Load things behind the navigation bars
                        or View.SYSTEM_UI_FLAG_FULLSCREEN) // Load things behind the navigation bars
        }
    }
}