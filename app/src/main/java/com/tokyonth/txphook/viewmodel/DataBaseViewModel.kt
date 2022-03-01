package com.tokyonth.txphook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tokyonth.txphook.db.HookAppInfo
import com.tokyonth.txphook.db.HookConfig
import com.tokyonth.txphook.db.HookDbManager
import com.tokyonth.txphook.db.HookRule
import kotlinx.coroutines.launch

class DataBaseViewModel(application: Application) : AndroidViewModel(application) {

    private val _hookAppInfoLiveData = MutableLiveData<MutableList<HookAppInfo>>()

    val hookAppInfoLiveData: MutableLiveData<MutableList<HookAppInfo>> = _hookAppInfoLiveData

    private val _pkgHookAppInfoLiveData = MutableLiveData<HookAppInfo>()

    val pkgHookAppInfoLiveData: MutableLiveData<HookAppInfo> = _pkgHookAppInfoLiveData

    fun getAllConfigData() {
        viewModelScope.launch {
            val allData = HookDbManager.get.getDao().queryAllConfig()
            _hookAppInfoLiveData.value = allData.toMutableList()
        }
    }

    fun getPkgRulesData(pkgName: String) {
        viewModelScope.launch {
            HookDbManager.get.getDao().queryRulesByPkg(pkgName)?.let {
                _pkgHookAppInfoLiveData.value = it
            }
        }
    }

    fun checkInsertConfigData(hookConfig: HookConfig) {
        viewModelScope.launch {
            val appConfig = HookDbManager.get.getDao().queryRulesByPkg(hookConfig.packageName)
            if (appConfig == null) {
                HookDbManager.get.getDao().insertConfig(hookConfig)
            } else {
                appConfig.config.apply {
                    appName = hookConfig.appName
                    packageName = hookConfig.packageName
                    appVersion = hookConfig.appVersion
                }
                HookDbManager.get.getDao().updateConfig(appConfig.config)
            }
        }
    }

    fun checkInsertRuleData(hookRule: HookRule) {
        viewModelScope.launch {
            val rule = HookDbManager.get.getDao().queryRule(hookRule.hookName)
            if (rule == null) {
                HookDbManager.get.getDao().insertRule(hookRule)
            } else {
                rule.apply {
                    enableHook = hookRule.enableHook
                    pkgName = hookRule.pkgName
                    hookName = hookRule.hookName
                    classPath = hookRule.classPath
                    methodName = hookRule.methodName
                    resultVale = hookRule.resultVale
                    valueType = hookRule.valueType
                }
                HookDbManager.get.getDao().updateRule(rule)
            }
        }
    }

    fun removeRuleData(hookRule: HookRule) {
        viewModelScope.launch {
            HookDbManager.get.getDao().deleteRule(hookRule)
        }
    }

    fun removeConfigData(hookConfig: HookConfig) {
        viewModelScope.launch {
            val appInfo = HookDbManager.get.getDao().queryRulesByPkg(hookConfig.packageName)
            if (appInfo != null) {
                HookDbManager.get.getDao().deleteConfig(appInfo.config)
            }
        }
    }

}
