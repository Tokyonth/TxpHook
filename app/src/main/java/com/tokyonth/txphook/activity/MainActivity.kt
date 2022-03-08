package com.tokyonth.txphook.activity

import android.app.ActivityOptions
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.tokyonth.txphook.Constants
import com.tokyonth.txphook.adapter.HookAppsAdapter
import com.tokyonth.txphook.databinding.ActivityMainBinding
import com.tokyonth.txphook.utils.PermissionUtils
import com.tokyonth.txphook.utils.json.HookConfigManager
import com.tokyonth.txphook.utils.ktx.lazyBind
import com.tokyonth.txphook.view.GridItemDecoration
import com.tokyonth.txphook.viewmodel.DataBaseViewModel
import com.tokyonth.txphook.widget.SheetDialog

class MainActivity : BaseActivity() {

    private val binding: ActivityMainBinding by lazyBind()

    private val model: DataBaseViewModel by viewModels()

    private lateinit var hookAdapter: HookAppsAdapter

    override fun setBinding() = binding

    override fun initData() {
        getExternalFilesDir("hooks")

        hookAdapter = HookAppsAdapter(this)

        model.hookAppInfoLiveData.observe(this) {
            hookAdapter.setData(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
        super.onCreate(savedInstanceState)
        PermissionUtils.register(this)
    }

    override fun initView() {
        val (status, color) = if (isModuleActive()) {
            Pair("已激活", Color.parseColor("#4CAF50"))
        } else {
            Pair("未激活", Color.parseColor("#F44336"))
        }
        binding.tvXpStatus.text = status
        binding.ivXpStatus.imageTintList = ColorStateList.valueOf(color)

        binding.rvHookApps.apply {
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            addItemDecoration(GridItemDecoration(24))
            adapter = hookAdapter
        }

        hookAdapter.setItemClick { _, hookConfig ->
            SheetDialog(this) {
                PermissionUtils.checkPermission {
                    val msg = if (it) {
                        val isSuccess = HookConfigManager.get.export(hookConfig)
                        if (isSuccess) {
                            "导出成功!"
                        } else {
                            "导出失败!"
                        }
                    } else {
                        "没有权限!"
                    }
                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                }
            }.apply {
                setHookInfo(hookConfig)
            }.show()
        }

        binding.fabAdd.setOnClickListener {
            it.transitionName = Constants.ELEMENT_CONTAINER_TRANSITION
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                it,
                Constants.ELEMENT_CONTAINER_TRANSITION
            )
            val listIntent = Intent(this, AppListActivity::class.java)
            startActivity(listIntent, options.toBundle())
        }
    }

/*    private fun importConfig(filePath: String) {
        HookConfigManager.import(filePath, {

        }, {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        })
    }*/

    private fun isModuleActive(): Boolean {
        return false
    }

    override fun onResume() {
        super.onResume()
        model.getAllConfigData()
    }

    override fun onDestroy() {
        super.onDestroy()
        PermissionUtils.unRegister()
    }

}
