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
import com.tokyonth.txphook.utils.pinyin.PinyinUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class InstalledAppViewModel : ViewModel() {

    private val _dataResultLiveData = MutableLiveData<MutableList<AppEntity>>()

    val dataResultLiveData: MutableLiveData<MutableList<AppEntity>> = _dataResultLiveData

    fun getApps() {
        doAsync {
            val apps = getInstallApps()
            onUI {
                dataResultLiveData.value = apps
            }
        }
    }

    private fun getInstallApps(): MutableList<AppEntity> {
        val packageManager = App.context.packageManager
        val packages: MutableList<AppEntity> = ArrayList()
        try {
            val packageInfoArr: List<PackageInfo> = packageManager.getInstalledPackages(0)
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
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        Collections.sort(packages,
            AppEntityComparator()
        )
        return packages
    }

    private fun isSystemApp(pi: PackageInfo): Boolean {
        val isSysApp = pi.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1
        val isSysUpd = pi.applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP == 1
        return isSysApp || isSysUpd
    }

    fun getGroupIndex(appEntityList: MutableList<AppEntity>): Map<Int, String> {
        val groupIndexMap: MutableMap<Int, String> = HashMap()
        if (appEntityList.isNotEmpty()) {
            val size = appEntityList.size
            val letter: String = PinyinUtils.getLetter(appEntityList[0].appName)
            groupIndexMap[0] = letter
            for (i in 1 until size) {
                val preContactBean: AppEntity = appEntityList[i - 1]
                val preLetter: String = PinyinUtils.getLetter(preContactBean.appName)
                val contactBean: AppEntity = appEntityList[i]
                val curLetter: String = PinyinUtils.getLetter(contactBean.appName)
                if (curLetter != preLetter) {
                    groupIndexMap[i] = curLetter
                }
            }
        }
        return groupIndexMap
    }

}
