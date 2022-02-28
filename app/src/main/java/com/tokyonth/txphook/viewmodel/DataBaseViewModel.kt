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

    private val _dataResultLiveData = MutableLiveData<MutableList<HookAppInfo>>()

    val dataResultLiveData: MutableLiveData<MutableList<HookAppInfo>> = _dataResultLiveData

    private val _ruleResultLiveData = MutableLiveData<HookAppInfo>()

    val ruleResultLiveData: MutableLiveData<HookAppInfo> = _ruleResultLiveData

    fun getAllConfigData() {
        viewModelScope.launch {
            val allData = HookDbManager.get.getDao().queryAllConfig()
            _dataResultLiveData.value = allData.toMutableList()
        }
    }

    fun getRuleData(pkgName: String) {
        viewModelScope.launch {
            HookDbManager.get.getDao().getRulesByPkg(pkgName)?.let {
                ruleResultLiveData.value = it
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
