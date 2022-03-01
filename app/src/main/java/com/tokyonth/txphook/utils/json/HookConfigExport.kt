package com.tokyonth.txphook.utils.json

import com.tokyonth.txphook.Constants
import com.tokyonth.txphook.db.HookAppInfo
import com.tokyonth.txphook.utils.file.FileUtils
import org.json.JSONArray
import org.json.JSONObject

object HookConfigExport {

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

}
