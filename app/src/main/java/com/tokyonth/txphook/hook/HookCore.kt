package com.tokyonth.txphook.hook

import android.app.Application
import android.content.Context
import com.tokyonth.txphook.Constants
import com.tokyonth.txphook.db.HookAppInfo
import com.tokyonth.txphook.db.HookDbManager
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kotlinx.coroutines.runBlocking

class HookCore {

    fun initCore(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookMethod(
            Application::class.java, "attach",
            Context::class.java, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    super.beforeHookedMethod(param)
                    var context: Context? = param.args[0] as Context
                    context = context!!.createPackageContext(
                        Constants.MY_PACKAGE_NAME,
                        Context.CONTEXT_INCLUDE_CODE
                    )
                    val hookAppInfoList = getAllHookApp(context)
                    for ((config, rule) in hookAppInfoList) {
                        if (lpparam.packageName == config.packageName) {
                            HookHelper().startHook(lpparam.classLoader, rule)
                        }
                    }
                }
            })
    }

    private fun getAllHookApp(context: Context): List<HookAppInfo> {
        val db = HookDbManager.of(context).getDao()
        return runBlocking {
            db.queryAllConfig()
        }
    }

}
