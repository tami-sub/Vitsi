package com.example.vitsi.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import androidx.core.content.ContextCompat
import com.example.vitsi.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import timber.log.Timber

object PermissionUtils {

    val recordVideoPermissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )

    fun isPermissionGranted(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    fun arePermissionsGranted(context: Context, permissionList: Collection<String>) =
        permissionList.all { permission -> isPermissionGranted(context, permission) }

    fun requestPermissions(
        context: Context,
        listener: MultiplePermissionsListener,
        permissionList: Collection<String>
    ) {
        Dexter.withContext(context)
            .withPermissions(permissionList)
            .withListener(listener)
            .withErrorListener {
                Timber.e(it.toString())
            }
            .check()
    }

    class DialogMultiplePermissionsListener(
        val view: View,
        val onPermissionsGranted: () -> Unit,
        val onPermissionsDenied: () -> Unit
    ) : BaseMultiplePermissionsListener() {
        val context: Context = view.context

        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
            Timber.d("it.areAllPermissionsGranted() is ${report?.areAllPermissionsGranted()} and permissions denied is ${report?.deniedPermissionResponses}")

            if (report?.areAllPermissionsGranted() == true) {
                ResUtils.showSnackBar(view, R.string.all_permissions_granted)
                onPermissionsGranted()
            } else {
                ResUtils.showSnackBar(view, R.string.permissions_denied)
                onPermissionsDenied()
            }
        }
    }

}