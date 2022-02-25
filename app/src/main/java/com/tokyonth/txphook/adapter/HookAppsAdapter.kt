package com.tokyonth.txphook.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tokyonth.txphook.databinding.ItemHookAppsBinding
import com.tokyonth.txphook.db.HookConfig
import com.tokyonth.txphook.utils.PackageUtils

class HookAppsAdapter : RecyclerView.Adapter<HookAppsAdapter.ViewHolder>() {

    private var dataArr: MutableList<HookConfig>? = null

    private var click: ((Int, HookConfig) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(dataArr: MutableList<HookConfig>) {
        this.dataArr = dataArr
        notifyDataSetChanged()
    }

    fun setItemClick(click: (Int, HookConfig) -> Unit) {
        this.click = click
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHookAppsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataArr?.get(position)
        if (data != null && click != null) {
            holder.bind(data, click!!)
        }
    }

    override fun getItemCount(): Int {
        return dataArr?.size ?: 0
    }

    class ViewHolder(private val binding: ItemHookAppsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context

        @SuppressLint("SetTextI18n")
        fun bind(hookConfig: HookConfig, click: ((Int, HookConfig) -> Unit)) {
            binding.run {
                itemTvName.text = hookConfig.appName
                itemTvVersion.text = PackageUtils.getVersionNameByPackageName(
                    context,
                    hookConfig.packageName
                )
                itemTvCurrVersion.append(hookConfig.appVersion)
                itemIvIcon.setImageDrawable(
                    PackageUtils.getAppIconByPackageName(
                        context,
                        hookConfig.packageName
                    )
                )
                itemTvRuleValue.text = "${hookConfig.hookAmount} 条Hook规则"
            }

            binding.root.setOnClickListener {
                click.invoke(adapterPosition, hookConfig)
            }
        }

    }

}