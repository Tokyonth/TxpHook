package com.tokyonth.txphook.hook;

import com.tokyonth.txphook.Constants;
import com.tokyonth.txphook.entry.HookAppsEntry;
import com.tokyonth.txphook.entry.HookInfoEntry;
import com.tokyonth.txphook.utils.json.HookConfigManager;

import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookApps implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        String pkg = Constants.MY_PACKAGE_NAME;
        if (lpparam.packageName.equals(pkg)) {
            XposedHelpers.findAndHookMethod(pkg + ".activity.MainActivity",
                    lpparam.classLoader,
                    "isModuleActive",
                    XC_MethodReplacement.returnConstant(true));
        }

        ArrayList<HookAppsEntry> appsEntries = HookConfigManager.getInstance().getAppConfig();
        for (HookAppsEntry hookAppsEntry : appsEntries) {
            if (lpparam.packageName.equals(hookAppsEntry.packageName) && hookAppsEntry.isHook) {
                startHook(lpparam.classLoader, hookAppsEntry.packageName);
            }
        }
    }

    private void startHook(ClassLoader classLoader, String packageName) {
        ArrayList<HookInfoEntry> hooks = HookConfigManager.getInstance().getHookConfig(packageName);
        for (HookInfoEntry hook : hooks) {
            XposedHelpers.findAndHookMethod(hook.classPath,
                    classLoader,
                    hook.methodName,
                    XC_MethodReplacement.returnConstant(hook.resultVale));

            //XC_MethodReplacement.DO_NOTHING
        }
    }

}
