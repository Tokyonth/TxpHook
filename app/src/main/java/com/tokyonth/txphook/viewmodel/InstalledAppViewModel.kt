package com.tokyonth.txphook.viewmodel

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tokyonth.txphook.App
import com.tokyonth.txphook.entry.AppEntry
import com.tokyonth.txphook.entry.AppEntryComparator
import com.tokyonth.txphook.utils.ktx.doAsync
import com.tokyonth.txphook.utils.ktx.onUI
import com.tokyonth.txphook.utils.pinyin.PinyinUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class InstalledAppViewModel : ViewModel() {

    private val _dataResultLiveData = MutableLiveData<MutableList<AppEntry>>()

    val dataResultLiveData: MutableLiveData<MutableList<AppEntry>> = _dataResultLiveData

    fun getApps() {
        doAsync {
            val apps = getInstallApps()
            onUI {
                dataResultLiveData.value = apps
            }
        }
    }

    private fun getInstallApps(): MutableList<AppEntry> {
        val packageManager = App.context.packageManager
        val packages: MutableList<AppEntry> = ArrayList()
        try {
            val packageInfoArr: List<PackageInfo> = packageManager.getInstalledPackages(0)
            for (info in packageInfoArr) {
                if (!isSystemApp(info)) {
                    val pkg = AppEntry(
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
        Collections.sort(packages, AppEntryComparator())
        return packages
    }

    private fun isSystemApp(pi: PackageInfo): Boolean {
        val isSysApp = pi.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1
        val isSysUpd = pi.applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP == 1
        return isSysApp || isSysUpd
    }

    fun getGroupIndex(appEntryList: MutableList<AppEntry>): Map<Int, String> {
        val groupIndexMap: MutableMap<Int, String> = HashMap()
        if (appEntryList.isNotEmpty()) {
            val size = appEntryList.size
            val letter: String = PinyinUtils.getLetter(appEntryList[0].appName)
            groupIndexMap[0] = letter
            for (i in 1 until size) {
                val preContactBean: AppEntry = appEntryList[i - 1]
                val preLetter: String = PinyinUtils.getLetter(preContactBean.appName)
                val contactBean: AppEntry = appEntryList[i]
                val curLetter: String = PinyinUtils.getLetter(contactBean.appName)
                if (curLetter != preLetter) {
                    groupIndexMap[i] = curLetter
                }
            }
        }
        return groupIndexMap
    }

}
