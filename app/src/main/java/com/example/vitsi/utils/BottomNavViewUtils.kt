package com.example.vitsi.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.vitsi.presentation.MainActivity
import com.example.vitsi.R

object BottomNavViewUtils {

    fun showBottomNavBar(activity: Activity?) = changeVisibility(activity, shouldShow = true)
    fun hideBottomNavBar(activity: Activity?) = changeVisibility(activity, shouldShow = false)

    fun changeVisibility(activity: Activity?, shouldShow: Boolean) {
        val bottomNavigationView = (activity as? MainActivity)?.binding?.navView
        bottomNavigationView?.visibility = if (shouldShow) View.VISIBLE else View.GONE
    }


    @SuppressLint("ResourceType")
    fun changeNavBarColor(activity: Activity?, systemBarColors: SystemBarColors) {
        val navView = (activity as MainActivity).binding.navView

        val backgroundRes =
            if (systemBarColors == SystemBarColors.WHITE) android.R.color.white
            else R.color.dark_black

        val iconRes =
            if (systemBarColors == SystemBarColors.WHITE) R.drawable.dark_nav_icon_tint
            else R.drawable.white_nav_icon_tint

        val iconTint = ResourcesCompat.getColorStateList(activity.resources, iconRes, null)

        navView.setBackgroundColor(backgroundRes)
        navView.itemIconTintList = iconTint
        navView.itemTextColor = iconTint
    }
}