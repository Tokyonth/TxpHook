package com.tokyonth.txphook.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

object PermissionUtils {

    private val permissionArr = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private var activity: AppCompatActivity? = null

    private val permissionStatus: ((Boolean) -> Unit)? = null

    private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null

    fun register(activity: AppCompatActivity) {
        this.activity = activity
        val contracts = ActivityResultContracts.RequestMultiplePermissions()
        permissionLauncher =
            activity.registerForActivityResult(contracts) {
                val success = it.filter { entry ->
                    !entry.value
                }.isEmpty()
                permissionStatus?.invoke(success)
            }
    }

    fun unRegister() {
        permissionLauncher?.unregister()
    }

    fun checkPermission(permissionStatus: (Boolean) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                permissionArr[0]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher?.launch(permissionArr)
        } else {
            permissionStatus.invoke(true)
        }
    }

}
