package com.tokyonth.txphook.hook

import com.tokyonth.txphook.Constants

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XC_MethodReplacement

class HookAppsHandle : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        val pkg = Constants.MY_PACKAGE_NAME
        if (lpparam.packageName == pkg) {
            XposedHelpers.findAndHookMethod(
                "$pkg.activity.MainActivity",
                lpparam.classLoader,
                "isModuleActive",
                XC_MethodReplacement.returnConstant(true)
            )
        } else {
            HookCore().initCore(lpparam)
        }
    }

}
