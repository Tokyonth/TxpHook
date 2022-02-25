package com.tokyonth.txphook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tokyonth.txphook.db.HookConfig
import com.tokyonth.txphook.db.HookDbInstance
import kotlinx.coroutines.launch

class DataBaseViewModel(application: Application) : AndroidViewModel(application) {

    private val _dataResultLiveData = MutableLiveData<MutableList<HookConfig>>()

    val dataResultLiveData: MutableLiveData<MutableList<HookConfig>> = _dataResultLiveData

    fun getAllData() {
        viewModelScope.launch {
            val allData = HookDbInstance.get.getDao().queryAll()
            _dataResultLiveData.value = allData.toMutableList()
        }
    }

    fun insertData(hookConfig: HookConfig) {
        viewModelScope.launch {
            HookDbInstance.get.getDao().insert(hookConfig)
        }
    }

    fun updateData(hookConfig: HookConfig) {
        viewModelScope.launch {
            HookDbInstance.get.getDao().update(hookConfig)
        }
    }

    fun removeData(hookConfig: HookConfig) {
        viewModelScope.launch {
            HookDbInstance.get.getDao().delete(hookConfig)
        }
    }

}
