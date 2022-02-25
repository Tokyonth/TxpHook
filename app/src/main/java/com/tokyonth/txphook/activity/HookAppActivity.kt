package com.tokyonth.txphook.activity

import android.annotation.SuppressLint
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tokyonth.txphook.Constants
import com.tokyonth.txphook.adapter.HookConfigAdapter
import com.tokyonth.txphook.databinding.ActivityHookAppBinding
import com.tokyonth.txphook.db.HookConfig
import com.tokyonth.txphook.entry.AppEntry
import com.tokyonth.txphook.entry.HookAppsEntry
import com.tokyonth.txphook.utils.PackageUtils
import com.tokyonth.txphook.utils.json.HookConfigManager
import com.tokyonth.txphook.utils.ktx.lazyBind
import com.tokyonth.txphook.viewmodel.DataBaseViewModel
import com.tokyonth.txphook.widget.InputDialog

class HookAppActivity : BaseActivity() {

    private val binding: ActivityHookAppBinding by lazyBind()

    private val model: DataBaseViewModel by viewModels()

    override fun setBinding() = binding

    private lateinit var appEntry: AppEntry

    private lateinit var configAdapter: HookConfigAdapter

    override fun initData() {
        intent.run {
            val pkgName = getStringExtra(Constants.INTENT_PACKAGE_KEY)!!
            appEntry = AppEntry(
                PackageUtils.getAppIconByPackageName(this@HookAppActivity, pkgName),
                getStringExtra(Constants.INTENT_APP_NAME_KEY)!!,
                getStringExtra(Constants.INTENT_APP_VERSION_KEY)!!,
                pkgName
            )
        }

        configAdapter = HookConfigAdapter(this)
        configAdapter.setData(
            HookConfigManager.getInstance()
                .getHookConfig(appEntry.packageName)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.run {
            ivAppIcon.setImageDrawable(appEntry.appIcon)
            tvAppInfo.text = "${appEntry.packageName} \n ${appEntry.appVersion}"
            tvAppName.text = appEntry.appName
        }

        binding.rvHookConfig.apply {
            layoutManager = GridLayoutManager(this@HookAppActivity, 1)
            adapter = configAdapter
        }

        configAdapter.setBtnClick { msg, position, type ->
            Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
            if (type == 0) {
                configAdapter.getData().removeAt(position)
                configAdapter.notifyItemRemoved(position)
            } else {
                val configEntry = HookAppsEntry().apply {
                    packageName = appEntry.packageName
                    isHook = true
                }
                HookConfigManager.getInstance().saveAppConfig(configEntry)

                val dbHookConfig = HookConfig(
                    0,
                    appEntry.appName,
                    appEntry.packageName,
                    appEntry.appVersion,
                    configAdapter.getData().size.toString()
                )
                //model.updateData(dbHookConfig)
                model.insertData(dbHookConfig)
            }
        }

        binding.fabAddHookRule.setOnClickListener {
/*            configAdapter.saveAll {
                MaterialAlertDialogBuilder(this)
                    .setTitle("提示")
                    .setMessage("不完整的Rule将被忽略: \n $it")
                    .setPositiveButton("确定", null)
                    .show()
            }*/

            InputDialog(this).of {
                configAdapter.addSimpleData(appEntry.packageName, it)
            }.show()
        }
    }

}
