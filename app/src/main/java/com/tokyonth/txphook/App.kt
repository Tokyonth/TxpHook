package com.tokyonth.txphook

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.tokyonth.txphook.utils.SPUtils

class App : Application() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set

    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        SPUtils.initSP(this, Constants.SP_FILE_NAME)
    }

}
