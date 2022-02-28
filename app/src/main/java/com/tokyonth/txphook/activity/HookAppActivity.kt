package com.tokyonth.txphook.activity

import android.annotation.SuppressLint
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tokyonth.txphook.Constants
import com.tokyonth.txphook.adapter.HookConfigAdapter
import com.tokyonth.txphook.databinding.ActivityHookAppBinding
import com.tokyonth.txphook.db.HookConfig
import com.tokyonth.txphook.db.HookRule
import com.tokyonth.txphook.entity.AppEntity
import com.tokyonth.txphook.utils.PackageUtils
import com.tokyonth.txphook.utils.ktx.lazyBind
import com.tokyonth.txphook.viewmodel.DataBaseViewModel
import com.tokyonth.txphook.widget.InputDialog

class HookAppActivity : BaseActivity() {

    private val binding: ActivityHookAppBinding by lazyBind()

    private val model: DataBaseViewModel by viewModels()

    override fun setBinding() = binding

    private lateinit var appEntity: AppEntity

    private lateinit var configAdapter: HookConfigAdapter

    override fun initData() {
        intent.run {
            val pkgName = getStringExtra(Constants.INTENT_PACKAGE_KEY)!!
            appEntity = AppEntity(
                PackageUtils.getAppIconByPackageName(this@HookAppActivity, pkgName),
                getStringExtra(Constants.INTENT_APP_NAME_KEY)!!,
                getStringExtra(Constants.INTENT_APP_VERSION_KEY)!!,
                pkgName
            )
        }

        configAdapter = HookConfigAdapter(this)

        model.getRuleData(appEntity.packageName)
        model.ruleResultLiveData.observe(this) {
            configAdapter.setData(it.rule.toMutableList())
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.run {
            ivAppIcon.setImageDrawable(appEntity.appIcon)
            tvAppInfo.text = "${appEntity.packageName} \n ${appEntity.appVersion}"
            tvAppName.text = appEntity.appName
        }

        binding.rvHookConfig.apply {
            layoutManager = GridLayoutManager(this@HookAppActivity, 1)
            adapter = configAdapter
        }

        configAdapter.setBtnClick { hookRule, position, type ->
            optRvClick(hookRule, position, type)
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
                configAdapter.addSimpleData(appEntity.packageName, it)
            }.show()
        }
    }

    private fun optRvClick(hookRule: HookRule?, position: Int, optType: Int) {
        val msg = if (optType == 0) {
            configAdapter.getData().removeAt(position)
            configAdapter.notifyItemRemoved(position)
            if (hookRule != null) {
                model.removeRuleData(hookRule)
            }
            if (configAdapter.getData().isEmpty()) {
                val config = HookConfig(
                    0,
                    appEntity.appName,
                    appEntity.packageName,
                    appEntity.appVersion
                )
                model.removeConfigData(config)
            }
            "删除成功!"
        } else {
            if (hookRule != null) {
                if (configAdapter.getData().size == 1) {
                    val config = HookConfig(
                        0,
                        appEntity.appName,
                        appEntity.packageName,
                        appEntity.appVersion
                    )
                    model.insertConfigData(config)
                }
                model.insertRuleData(hookRule)
                "保存成功!"
            } else {
                "Rule不完整!"
            }
        }
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    }

}
