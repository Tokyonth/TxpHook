package com.tokyonth.txphook.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tokyonth.txphook.adapter.HookAppsAdapter
import com.tokyonth.txphook.databinding.ActivityMainBinding
import com.tokyonth.txphook.utils.file.ContentPathUtils
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

    private val safFile = registerForActivityResult(ActivityResultContracts.GetContent()) {
        val path = ContentPathUtils(
            packageName
        ).getFile(this, it)
        Log.e("打印-->", path.absolutePath)
    }

    override fun initData() {
        getExternalFilesDir("hooks")

        getIndex()

        hookAdapter = HookAppsAdapter(this)

        model.hookAppInfoLiveData.observe(this) {
            hookAdapter.setData(it)
        }
    }

    private fun getIndex() {
        val indexMap = HashMap<Int, String>()
        val array = "AABBCCCCCDEEF".map {
            it.toString()
        }
        indexMap[0] = array[0]
        for (i in 1 until array.size) {
            val l = array[i]
            if (l != array[i - 1]) {
                indexMap[i] = l
            }
        }

        for (map in indexMap) {
            Log.e("打印-->", "值: ${map.value}, 第一次下标: ${map.key}")
        }
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
                HookConfigManager.of(this) {
                    val msg = it?.export(hookConfig)?.let { isSuccess ->
                        if (isSuccess) {
                            "导出成功!"
                        } else {
                            "导出失败!"
                        }
                    } ?: "没有权限!"
                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                }
            }.apply {
                setHookInfo(hookConfig)
            }.show()
        }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AppListActivity::class.java))
        }

        binding.fabAdd.setOnLongClickListener {
            safFile.launch("application/json")
            true
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

}
