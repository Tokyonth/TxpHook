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
            val allData = HookDbManager.instance.getDao().queryAllConfig()
            _hookAppInfoLiveData.value = allData.toMutableList()
        }
    }

    fun getPkgRulesData(pkgName: String) {
        viewModelScope.launch {
            HookDbManager.instance.getDao().queryRulesByPkg(pkgName)?.let {
                _pkgHookAppInfoLiveData.value = it
            }
        }
    }

    fun checkInsertConfigData(hookConfig: HookConfig) {
        viewModelScope.launch {
            val appConfig = HookDbManager.instance.getDao().queryRulesByPkg(hookConfig.packageName)
            if (appConfig == null) {
                HookDbManager.instance.getDao().insertConfig(hookConfig)
            } else {
                appConfig.config.apply {
                    appName = hookConfig.appName
                    packageName = hookConfig.packageName
                    appVersion = hookConfig.appVersion
                }
                HookDbManager.instance.getDao().updateConfig(appConfig.config)
            }
        }
    }

    fun checkInsertRuleData(hookRule: HookRule) {
        viewModelScope.launch {
            val rule = HookDbManager.instance.getDao()
                .queryRule(hookRule.hookName, hookRule.pkgName)
            if (rule == null) {
                HookDbManager.instance.getDao().insertRule(hookRule)
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
                HookDbManager.instance.getDao().updateRule(rule)
            }
        }
    }

    fun removeRuleData(hookRule: HookRule) {
        viewModelScope.launch {
            HookDbManager.instance.getDao().deleteRule(hookRule)
        }
    }

    fun removeConfigData(hookConfig: HookConfig) {
        viewModelScope.launch {
            val appInfo = HookDbManager.instance.getDao().queryRulesByPkg(hookConfig.packageName)
            if (appInfo != null) {
                HookDbManager.instance.getDao().deleteConfig(appInfo.config)
            }
        }
    }

}
