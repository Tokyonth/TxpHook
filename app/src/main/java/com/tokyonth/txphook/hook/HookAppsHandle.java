package com.tokyonth.txphook.hook;

import com.tokyonth.txphook.Constants;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookAppsHandle implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        String pkg = Constants.MY_PACKAGE_NAME;
        if (lpparam.packageName.equals(pkg)) {
            XposedHelpers.findAndHookMethod(pkg + ".activity.MainActivity",
                    lpparam.classLoader,
                    "isModuleActive",
                    XC_MethodReplacement.returnConstant(true));
        } else {
            new HookCore().initCore(lpparam);
        }
    }

}
