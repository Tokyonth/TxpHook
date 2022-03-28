package com.tokyonth.txphook.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.fragment.app.Fragment

/**
 * SPUtils By Tokyonth
 * 2022/3/23
 */
object SPUtils {

    private var USER: String? = null
    private var APP: Application? = null

    fun initSP(application: Application, spName: String) {
        this.APP = application
        this.USER = spName
    }

    /**
     * 添加
     */
    fun <T> Any.putSP(key: String, value: T, commit: Boolean = true) {
        putSP(getSharedPreferences(this), key, value, commit = commit)
    }

    /**
     * 获取
     */
    fun <T> Any.getSP(key: String, defValue: T): T {
        return getSP(getSharedPreferences(this), key, defValue)
    }

    /**
     * 删除
     */
    fun Any.delSP(key: String) {
        getSharedPreferences(this).edit(commit = true) {
            remove(key)
        }
    }

    private fun getSharedPreferences(any: Any): SharedPreferences {
        if (USER == null) {
            throw IllegalStateException("SharedPreference file name is null!")
        }
        val ctx = when (any) {
            is Activity -> any
            is Context -> any
            is Fragment -> any.requireContext()
            else -> {
                if (APP == null) {
                    throw IllegalStateException("Application context is null!")
                } else {
                    APP!!.applicationContext
                }
            }
        }
        return ctx.getSharedPreferences(USER, Context.MODE_PRIVATE)
    }

    private fun <T> putSP(
        mShareConfig: SharedPreferences,
        key: String,
        value: T,
        commit: Boolean = true
    ) {
        mShareConfig.edit(commit = commit) {
            when (value) {
                is String -> putString(key, value)
                is Long -> putLong(key, value)
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getSP(mShareConfig: SharedPreferences, key: String, defValue: T): T {
        return when (defValue) {
            is String -> mShareConfig.getString(key, defValue)
            is Long ->
                java.lang.Long.valueOf(mShareConfig.getLong(key, defValue))
            is Boolean ->
                java.lang.Boolean.valueOf(
                    mShareConfig.getBoolean(
                        key,
                        defValue
                    )
                )
            is Int -> Integer.valueOf(mShareConfig.getInt(key, defValue))
            is Float ->
                java.lang.Float.valueOf(mShareConfig.getFloat(key, defValue))
            else -> {
                mShareConfig.getString(key, defValue.toString())
            }
        } as T
    }

}
