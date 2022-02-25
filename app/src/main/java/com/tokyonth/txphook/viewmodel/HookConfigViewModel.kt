package com.tokyonth.txphook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tokyonth.txphook.db.HookDbInstance
import kotlinx.coroutines.launch

class HookConfigViewModel : ViewModel() {

    fun getAllConfig() {
        viewModelScope.launch {
            val allData = HookDbInstance.get.getDao().queryAll()

        }
    }

}
