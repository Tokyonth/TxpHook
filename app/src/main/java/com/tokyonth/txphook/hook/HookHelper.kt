package com.tokyonth.txphook.hook

import com.tokyonth.txphook.db.HookRule
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

class HookHelper {

    fun startHook(classLoader: ClassLoader, rules: List<HookRule>) {
        for ((_, enableHook, _, _, classPath, methodName, resultVale) in rules) {
            if (enableHook) {
                var hookRep: Any? = XC_MethodReplacement.returnConstant(resultVale)
                if (resultVale == "DO_NOTHING") {
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
