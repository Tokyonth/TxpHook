package com.tokyonth.txphook.hook

import com.tokyonth.txphook.db.HookRule
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

class HookHelper {

    fun startHook(classLoader: ClassLoader, rules: List<HookRule>) {
        for ((_, enableHook, _, _, classPath, methodName, resultVale, valueType) in rules) {
            if (enableHook) {
                val hookReturnValue = ParseDataType.pares(valueType, resultVale)
                var hookRep: Any? = XC_MethodReplacement.returnConstant(hookReturnValue)
                if (hookReturnValue == "XP_NOT") {
                    hookRep = XC_MethodReplacement.DO_NOTHING
                }
                XposedHelpers.findAndHookMethod(
                    classPath,
                    classLoader,
                    methodName,
                    hookRep
                )
            }
        }
    }

}
