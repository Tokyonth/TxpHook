package com.tokyonth.txphook.activity

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.util.Pair
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.tokyonth.txphook.Constants
import com.tokyonth.txphook.R
import com.tokyonth.txphook.adapter.InstalledAppAdapter
import com.tokyonth.txphook.databinding.ActivityAppListBinding
import com.tokyonth.txphook.utils.ktx.lazyBind
import com.tokyonth.txphook.viewmodel.InstalledAppViewModel

class AppListActivity : BaseActivity() {

    private val binding: ActivityAppListBinding by lazyBind()

    private val model: InstalledAppViewModel by viewModels()

    private val appsAdapter = InstalledAppAdapter()

    override fun setBinding() = binding

    override fun initData() {
        model.getApps()
    }

    override fun initView() {
        showToolbar()

        binding.refreshLayout.isRefreshing = true
        val appsLayoutManager = GridLayoutManager(this@AppListActivity, 1)
        binding.rvInstallApps.apply {
            layoutManager = appsLayoutManager
            adapter = appsAdapter
        }
        appsAdapter.setItemClick { views, _, appEntity ->
            val iconKey = Constants.SHARE_ICON_TRANSITION
            val nameKey = Constants.SHARE_NAME_TRANSITION
            views.first.transitionName = iconKey
            views.second.transitionName = nameKey
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                Pair(views.first, iconKey),
                Pair(views.second, nameKey)
            )

            Intent(this, HookAppActivity::class.java).apply {
                putExtra(Constants.INTENT_PACKAGE_KEY, appEntity.packageName)
                putExtra(Constants.INTENT_APP_NAME_KEY, appEntity.appName)
                putExtra(Constants.INTENT_APP_VERSION_KEY, appEntity.appVersion)
                startActivity(this, options.toBundle())
            }
        }

        binding.sideBarApp.setOnTouchLetterChangeListener {
            val pos: Int =
                appsAdapter.getLetterPosition(it) ?: return@setOnTouchLetterChangeListener
            if (pos != -1) {
                appsLayoutManager.scrollToPositionWithOffset(pos, 0)
            }
        }

        binding.refreshLayout.setOnRefreshListener {
            model.getApps()
        }

        model.dataResultLiveData.observe(this) {
            appsAdapter.setData(it)
            appsAdapter.setAppGroupMap(
                model.getGroupIndex(it)
            )
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun showToolbar() {
        setSupportActionBar(binding.toolBar)
        supportActionBar.let {
            if (it != null) {
                this.title = "应用列表"
                it.setHomeButtonEnabled(true)
                it.setDisplayHomeAsUpEnabled(true)
                val icon = ContextCompat.getDrawable(this, R.drawable.ic_round_back_24)?.apply {
                    if (isLightStatusBar()) {
                        setTint(Color.BLACK)
                    } else {
                        setTint(Color.WHITE)
                    }
                }
                binding.toolBar.navigationIcon = icon
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            //finish()
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
