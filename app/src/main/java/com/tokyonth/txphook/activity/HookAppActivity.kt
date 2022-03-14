package com.tokyonth.txphook.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Window
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.tokyonth.txphook.Constants
import com.tokyonth.txphook.adapter.HookConfigAdapter
import com.tokyonth.txphook.databinding.ActivityHookAppBinding
import com.tokyonth.txphook.db.HookConfig
import com.tokyonth.txphook.db.HookRule
import com.tokyonth.txphook.entity.AppEntity
import com.tokyonth.txphook.hook.ParseDataType
import com.tokyonth.txphook.utils.PackageUtils
import com.tokyonth.txphook.utils.ktx.lazyBind
import com.tokyonth.txphook.viewmodel.DataBaseViewModel
import com.tokyonth.txphook.widget.InputDialog

class HookAppActivity : BaseActivity() {

    private val binding: ActivityHookAppBinding by lazyBind()

    private val model: DataBaseViewModel by viewModels()

    override fun setBinding() = binding

    private lateinit var appEntity: AppEntity

    private val configAdapter = HookConfigAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        binding.ivAppIcon.transitionName = Constants.SHARE_ICON_TRANSITION
        binding.tvAppName.transitionName = Constants.SHARE_NAME_TRANSITION
        super.onCreate(savedInstanceState)
    }

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

        model.getPkgRulesData(appEntity.packageName)
        model.pkgHookAppInfoLiveData.observe(this) {
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
            InputDialog(this).of { name ->
                configAdapter.getData().forEach {
                    if (it.hookName == name) {
                        Snackbar.make(binding.root, "已存在相同Rule", Snackbar.LENGTH_SHORT).show()
                        return@of
                    }
                }
                configAdapter.addSimpleData(appEntity.packageName, name)
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
                configAdapter.notifyItemRemoved(0)
                model.removeConfigData(config)
            }
            "删除成功!"
        } else {
            if (hookRule != null) {
                val config = HookConfig(
                    0,
                    appEntity.appName,
                    appEntity.packageName,
                    appEntity.appVersion
                )

                if (verifyDataType(hookRule)) {
                    model.checkInsertConfigData(config)
                } else {
                    return
                }
                "保存成功!"
            } else {
                "Rule不完整!"
            }
        }
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    }

    private fun verifyDataType(hookRule: HookRule): Boolean {
        return try {
            ParseDataType.pares(hookRule.valueType, hookRule.resultVale)
            model.checkInsertRuleData(hookRule)
            true
        } catch (e: NumberFormatException) {
            errorDialog(e.message)
            false
        } catch (e: IllegalArgumentException) {
            errorDialog(e.message)
            false
        }
    }

    private fun errorDialog(msg: String?) {
        MaterialAlertDialogBuilder(this)
            .setTitle("错误")
            .setMessage("你的返回值与类型不匹配!\n $msg")
            .setPositiveButton("确定", null)
            .show()
    }

}
