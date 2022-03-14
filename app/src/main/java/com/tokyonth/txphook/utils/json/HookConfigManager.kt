package com.tokyonth.txphook.utils.json

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import com.tokyonth.txphook.Constants
import com.tokyonth.txphook.db.HookAppInfo
import com.tokyonth.txphook.db.HookConfig
import com.tokyonth.txphook.db.HookRule
import com.tokyonth.txphook.utils.file.FileUtils
import com.tokyonth.txphook.utils.permission.PermissionUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class HookConfigManager {

    companion object {

        private var hookConfigManager: HookConfigManager? = null

        private val permissionArray = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        fun of(activity: AppCompatActivity, instance: (HookConfigManager?) -> Unit) {
            PermissionUtils(activity)
                .request(*permissionArray) { areGrantedAll, _ ->
                    if (areGrantedAll) {
                        if (hookConfigManager == null) {
                            hookConfigManager = HookConfigManager()
                        }
                        instance.invoke(hookConfigManager)
                    } else {
                        instance.invoke(null)
                    }
                }
        }

    }

    fun export(hookAppInfo: HookAppInfo): Boolean {
        val config = JSONObject().apply {
            put("appName", hookAppInfo.config.appName)
            put("appVersion", hookAppInfo.config.appVersion)
            put("packageName", hookAppInfo.config.packageName)
        }

        val rules = JSONArray()
        hookAppInfo.rule.forEach {
            val rule = JSONObject().apply {
                put("enableHook", it.enableHook)
                put("pkgName", it.pkgName)
                put("hookName", it.hookName)
                put("classPath", it.classPath)
                put("methodName", it.methodName)
                put("resultVale", it.resultVale)
                put("valueType", it.valueType)
            }
            rules.put(rule)
        }
        val result = JSONObject().apply {
            put("config", config)
            put("rules", rules)
        }

        val fileName = hookAppInfo.config.packageName + ".json"
        FileUtils.write(result.toString(), Constants.HOOK_JSON_PATH, fileName)
        return true
    }

    fun import(filePath: String, success: (HookAppInfo) -> Unit, error: (String) -> Unit) {
        val jsonContent = FileUtils.read(File(filePath))
        if (jsonContent.isEmpty()) {
            error.invoke("配置为空!")
            return
        }
        try {
            val json = JSONObject(jsonContent)
            val config = json.getJSONObject("config")
            val rules = json.getJSONArray("rules")

            val pkgName = config.getString("packageName")
            val appName = config.getString("appName")
            val appVersion = config.getString("appVersion")
            val configEntity = HookConfig(0, appName, pkgName, appVersion)

            val resultRule: MutableList<HookRule> = ArrayList()
            for (index in 0 until rules.length()) {
                val obj = rules.getJSONObject(index)
                val enableHook = obj.getBoolean("enableHook")
                val pkgNameR = obj.getString("pkgName")
                val hookName = obj.getString("hookName")
                val classPath = obj.getString("classPath")
                val methodName = obj.getString("methodName")
                val resultVale = obj.getString("resultVale")
                val valueType = obj.getInt("valueType")

                val rule = HookRule(
                    0,
                    enableHook,
                    pkgNameR,
                    hookName,
                    classPath,
                    methodName,
                    resultVale,
                    valueType
                )
                resultRule.add(rule)
            }
            val hookAppInfo = HookAppInfo(configEntity, resultRule)
            success.invoke(hookAppInfo)
        } catch (e: JSONException) {
            error.invoke("配置解析错误!")
        }
    }

}
