package com.tokyonth.txphook.utils

import android.Manifest
import android.os.Bundle
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

object PermissionUtils {

    private val permissionArr = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private var singleFragment: PermissionFragment? = null

    fun checkPermission(activity: AppCompatActivity, result: (Boolean) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                activity,
                permissionArr[0]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            createFragment(activity).checkAndObs(result)
        } else {
            result.invoke(true)
        }
    }

    private fun createFragment(activity: AppCompatActivity): PermissionFragment {
        val fragmentManager: FragmentManager = activity.supportFragmentManager
        val fragmentName = activity.javaClass.simpleName
        if (singleFragment == null) {
            singleFragment = PermissionFragment()
            fragmentManager.beginTransaction()
                .add(singleFragment!!, fragmentName)
                .commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()
        }
        return singleFragment!!
    }

    class PermissionFragment : Fragment() {

        private var permissionStatus: ((Boolean) -> Unit)? = null

        private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null

        fun checkAndObs(permissionStatus: (Boolean) -> Unit) {
            this.permissionStatus = permissionStatus
            permissionLauncher?.launch(permissionArr)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
            permissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                    val success = it.filter { entry ->
                        !entry.value
                    }.isEmpty()
                    permissionStatus?.invoke(success)
                }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            permissionLauncher?.unregister()
        }

    }

}
