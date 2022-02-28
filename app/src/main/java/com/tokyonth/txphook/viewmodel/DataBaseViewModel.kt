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

    private val _hookRuleLiveData = MutableLiveData<HookRule>()

    val hookRuleLiveData: MutableLiveData<HookRule> = _hookRuleLiveData

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

    fun getRuleData(name: String) {
        viewModelScope.launch {
            HookDbManager.get.getDao().queryRule(name)?.let {
                _hookRuleLiveData.value = it
            }
        }
    }

    fun insertConfigData(hookConfig: HookConfig) {
        viewModelScope.launch {
            HookDbManager.get.getDao().insertConfig(hookConfig)
        }
    }

    fun insertRuleData(hookRule: HookRule) {
        viewModelScope.launch {
            HookDbManager.get.getDao().insertRule(hookRule)
        }
    }

    fun updateData(hookConfig: HookConfig) {
        viewModelScope.launch {
            HookDbManager.get.getDao().update(hookConfig)
        }
    }

    fun removeRuleData(hookRule: HookRule) {
        viewModelScope.launch {
            HookDbManager.get.getDao().deleteRule(hookRule)
        }
    }

    fun removeConfigData(hookConfig: HookConfig) {
        viewModelScope.launch {
            HookDbManager.get.getDao().deleteConfig(hookConfig)
        }
    }

}
