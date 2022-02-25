package com.tokyonth.txphook

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class App : Application() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set

    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}
