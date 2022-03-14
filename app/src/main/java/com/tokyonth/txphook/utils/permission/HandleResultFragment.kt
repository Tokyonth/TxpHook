package com.tokyonth.txphook.utils.permission

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

class HandleResultFragment : Fragment() {

    companion object {

        private const val PERMISSION_REQUEST_CODE = 10

        val TAG = toString()

    }

    private val mRequestSubjects: HashMap<String, (Permission) -> Unit> = HashMap()

    private var onFragmentCreated: (() -> Unit)? = null

    private var shareResultCallback: ((requestCode: Int, resultCode: Int, data: Intent?) -> Unit)? =
        null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        onFragmentCreated?.invoke()
        onFragmentCreated = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun request(permissions: Array<out String>) {
        if (isAdded) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE)
        } else {
            onFragmentCreated = {
                requestPermissions(permissions, PERMISSION_REQUEST_CODE)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            permissions.forEachIndexed { index, permission ->
                val permissionSubject = mRequestSubjects[permission]
                val permissionResult =
                    if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                        Permission(permission, granted = true, preventAskAgain = false)
                    } else {
                        Permission(
                            permission,
                            false,
                            !shouldShowRequestPermissionRationale(permission)
                        )
                    }
                permissionSubject?.invoke(permissionResult)
                mRequestSubjects.remove(permission)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        shareResultCallback?.invoke(requestCode, resultCode, data)
    }

    fun addPermissionSubject(permission: String, subject: (Permission) -> Unit) {
        mRequestSubjects[permission] = subject
    }

    fun getPermissionSubject(permission: String): ((Permission) -> Unit)? =
        mRequestSubjects[permission]

}
