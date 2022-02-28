package com.tokyonth.txphook.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.tokyonth.txphook.adapter.HookAppsAdapter
import com.tokyonth.txphook.databinding.ActivityMainBinding
import com.tokyonth.txphook.utils.ktx.lazyBind
import com.tokyonth.txphook.view.GridItemDecoration
import com.tokyonth.txphook.viewmodel.DataBaseViewModel

class MainActivity : BaseActivity() {

    private val binding: ActivityMainBinding by lazyBind()

    private val model: DataBaseViewModel by viewModels()

    private lateinit var hookAdapter: HookAppsAdapter

    override fun setBinding() = binding

    override fun initData() {
        getExternalFilesDir("hooks")

        hookAdapter = HookAppsAdapter(this)

        model.getAllConfigData()
        model.hookAppInfoLiveData.observe(this) {
            hookAdapter.setData(it)
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

        hookAdapter.setItemClick { position, hookConfig ->

        }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AppListActivity::class.java))
        }
    }

    private fun isModuleActive(): Boolean {
        return false
    }

}
