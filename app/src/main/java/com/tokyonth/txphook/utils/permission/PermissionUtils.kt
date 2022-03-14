package com.tokyonth.txphook.utils.permission

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.FragmentActivity

class PermissionUtils constructor(activity: FragmentActivity) {

    private var requestFragment: HandleResultFragment

    init {
        val fragment = activity.supportFragmentManager.findFragmentByTag(HandleResultFragment.TAG)
        if (fragment == null) {
            requestFragment = HandleResultFragment()
            activity
                .supportFragmentManager
                .beginTransaction()
                .add(requestFragment, HandleResultFragment.TAG)
                .commitAllowingStateLoss()
        } else {
            requestFragment = fragment as HandleResultFragment
        }
    }

    fun request(
        vararg permissions: String,
        callback: (areGrantedAll: Boolean, deniedPermissions: List<Permission>) -> Unit
    ) {
        val permissionSize = permissions.size
        val permissionsResult = arrayListOf<Permission>()

        val unrequestedPermission = arrayListOf<String>()
        permissions.forEach { permission ->
            if (isGranted(permission)) {
                permissionsResult.add(
                    Permission(
                        permission,
                        granted = true,
                        preventAskAgain = false
                    )
                )
            } else {
                var subject = requestFragment.getPermissionSubject(permission)
                if (subject == null) {
                    subject = fun(p: Permission) {
                        permissionsResult.add(p)
                        if (permissionsResult.size == permissionSize) {
                            val grantedAll = permissionsResult.all { it.granted }
                            if (grantedAll) {
                                callback.invoke(grantedAll, emptyList())
                            } else {
                                val permissionsNotGranted = permissionsResult.filter { !it.granted }
                                callback.invoke(grantedAll, permissionsNotGranted)
                            }
                        }
                    }
                }
                requestFragment.addPermissionSubject(permission, subject)
                unrequestedPermission.add(permission)
            }
        }

        if (unrequestedPermission.isNotEmpty() && !beforeAndroid6()) {
            requestFragment.request(unrequestedPermission.toTypedArray())
        } else {
            callback.invoke(true, emptyList())
        }
    }

    private fun isGranted(permission: String): Boolean {
        return requestFragment.activity?.isPermissionsGranted(permission) ?: false
    }
}

fun Context.isPermissionsGranted(vararg permissions: String): Boolean {
    if (beforeAndroid6()) return true

    permissions.forEach { permission ->
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

fun beforeAndroid6(): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.M
