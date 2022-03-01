package com.tokyonth.txphook.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tokyonth.txphook.Constants
import com.tokyonth.txphook.R
import com.tokyonth.txphook.activity.HookAppActivity
import com.tokyonth.txphook.databinding.LayoutDialogSheetBinding
import com.tokyonth.txphook.db.HookAppInfo
import com.tokyonth.txphook.utils.PackageUtils

class SheetDialog(private val ctx: Context) : BottomSheetDialog(ctx, R.style.BottomSheetDialog) {

    private val binding = LayoutDialogSheetBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    @SuppressLint("SetTextI18n")
    fun setHookInfo(hookAppInfo: HookAppInfo) {
        val icon = PackageUtils.getAppIconByPackageName(context, hookAppInfo.config.packageName)
        binding.apply {
            dialogIvIcon.setImageDrawable(icon)
            dialogTvAppMsg.text = hookAppInfo.config.appName
            dialogTvRules.text = "${hookAppInfo.rule.size} 条Hook规则"
        }

        binding.dialogButtonToHookInfo.setOnClickListener {
            val config = hookAppInfo.config
            Intent(ctx, HookAppActivity::class.java).apply {
                putExtra(Constants.INTENT_PACKAGE_KEY, config.packageName)
                putExtra(Constants.INTENT_APP_NAME_KEY, config.appName)
                putExtra(Constants.INTENT_APP_VERSION_KEY, config.appVersion)
                ctx.startActivity(this)
            }
            dismiss()
        }
    }

}
