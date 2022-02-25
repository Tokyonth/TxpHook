package com.tokyonth.txphook.utils.json;

import com.tokyonth.txphook.Constants;
import com.tokyonth.txphook.entry.HookAppsEntry;
import com.tokyonth.txphook.entry.HookInfoEntry;
import com.tokyonth.txphook.utils.file.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HookConfigManager {

    private static HookConfigManager instance;

    public static HookConfigManager getInstance() {
        if (instance == null) {
            instance = new HookConfigManager();
        }
        return instance;
    }

    public ArrayList<HookAppsEntry> getAppConfig() {
        File jsonFile = new File(Constants.HOOK_JSON_PATH + Constants.HOOK_APPS_CONFIG);

        String appsConfig = FileUtils.read(jsonFile);
        ArrayList<HookAppsEntry> configList = new ArrayList<>();
        if (!appsConfig.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(appsConfig);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    HookAppsEntry hookApps = new HookAppsEntry();
                    hookApps.packageName = jsonObject.getString("packageName");
                    hookApps.isHook = jsonObject.getBoolean("isHook");
                    configList.add(hookApps);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return configList;
    }

    public void saveAppConfig(HookAppsEntry appsEntry) {
        String jsonPath = Constants.HOOK_JSON_PATH + Constants.HOOK_APPS_CONFIG;
        String appsConfig = FileUtils.read(new File(jsonPath));

        try {
            JSONArray jsonArray;
            if (appsConfig.isEmpty()) {
                jsonArray = new JSONArray();
            } else {
                jsonArray = new JSONArray(appsConfig);
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("packageName", appsEntry.packageName);
            jsonObject.put("isHook", appsEntry.isHook);
            jsonArray.put(jsonObject);
            FileUtils.write(jsonArray.toString(), Constants.HOOK_JSON_PATH, Constants.HOOK_APPS_CONFIG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<HookInfoEntry> getHookConfig(String pkgName) {
        String jsonPath = Constants.HOOK_JSON_PATH + pkgName + ".json";
        String appsConfig = FileUtils.read(new File(jsonPath));
        ArrayList<HookInfoEntry> configList = new ArrayList<>();
        if (!appsConfig.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(appsConfig);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    HookInfoEntry config = new HookInfoEntry();
                    config.packageName = jsonObject.getString("packageName");
                    config.hookName = jsonObject.getString("hookName");
                    config.classPath = jsonObject.getString("classPath");
                    config.methodName = jsonObject.getString("methodName");
                    config.resultVale = jsonObject.get("resultVale");
                    configList.add(config);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return configList;
    }

    public void removeHookConfig(String pkgName, int index) {
        String jsonPath = Constants.HOOK_JSON_PATH + pkgName + ".json";
        String appsConfig = FileUtils.read(new File(jsonPath));
        if (appsConfig.isEmpty())
            return;
        try {
            JSONArray jsonArray = new JSONArray(appsConfig);
            jsonArray.remove(index);
            String fileName = pkgName + ".json";
            FileUtils.write(jsonArray.toString(), Constants.HOOK_JSON_PATH, fileName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveHookConfig(HookInfoEntry hookInfoEntry) {
        String jsonPath = Constants.HOOK_JSON_PATH + hookInfoEntry.packageName + ".json";
        String appsConfig = FileUtils.read(new File(jsonPath));

        try {
            JSONArray jsonArray;
            if (appsConfig.isEmpty()) {
                jsonArray = new JSONArray();
            } else {
                jsonArray = new JSONArray(appsConfig);
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("packageName", hookInfoEntry.packageName);
            jsonObject.put("hookName", hookInfoEntry.hookName);
            jsonObject.put("classPath", hookInfoEntry.classPath);
            jsonObject.put("methodName", hookInfoEntry.methodName);
            jsonObject.put("resultVale", hookInfoEntry.resultVale);
            jsonArray.put(jsonObject);
            String fileName = hookInfoEntry.packageName + ".json";
            FileUtils.write(jsonArray.toString(), Constants.HOOK_JSON_PATH, fileName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveAllHookConfigs(List<HookInfoEntry> hookInfoEntries) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (HookInfoEntry hookInfoEntry : hookInfoEntries) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("packageName", hookInfoEntry.packageName);
                jsonObject.put("hookName", hookInfoEntry.hookName);
                jsonObject.put("classPath", hookInfoEntry.classPath);
                jsonObject.put("methodName", hookInfoEntry.methodName);
                jsonObject.put("resultVale", hookInfoEntry.resultVale);
                jsonArray.put(jsonObject);
            }
            String fileName = hookInfoEntries.get(0).packageName + ".json";
            FileUtils.write(jsonArray.toString(), Constants.HOOK_JSON_PATH, fileName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
