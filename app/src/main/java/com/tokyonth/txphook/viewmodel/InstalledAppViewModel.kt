package com.tokyonth.txphook.viewmodel

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.tokyonth.txphook.App
import com.tokyonth.txphook.entity.AppEntity
import com.tokyonth.txphook.entity.AppEntityComparator
import com.tokyonth.txphook.utils.ktx.doAsync
import com.tokyonth.txphook.utils.ktx.onUI

import java.util.*
import kotlin.collections.ArrayList

class InstalledAppViewModel : ViewModel() {

    private val _dataResultLiveData = MutableLiveData<MutableList<AppEntity>>()

    val dataResultLiveData: MutableLiveData<MutableList<AppEntity>> = _dataResultLiveData

    fun getInstalledApps() {
        doAsync {
            val apps = getSortInstallApps()
            onUI {
                dataResultLiveData.value = apps
            }
        }
    }

    private fun getSortInstallApps(): MutableList<AppEntity> {
        val packageManager = App.context.packageManager
        val packages: MutableList<AppEntity> = ArrayList()
        try {
            val packageInfoArr = packageManager.getInstalledPackages(0)
            for (info in packageInfoArr) {
                if (!isSystemApp(info)) {
                    val pkg = AppEntity(
                        packageManager.getApplicationIcon(info.applicationInfo),
                        packageManager.getApplicationLabel(info.applicationInfo).toString(),
                        info.versionName,
                        info.packageName
                    )
                    packages.add(pkg)
                }
            }
            Collections.sort(
                packages,
                AppEntityComparator()
            )
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return packages
    }

    private fun isSystemApp(pi: PackageInfo): Boolean {
        val isSysApp = pi.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1
        val isSysUpd = pi.applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP == 1
        return isSysApp || isSysUpd
    }

}
