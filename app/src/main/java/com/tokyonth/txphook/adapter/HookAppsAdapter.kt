package com.tokyonth.txphook.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tokyonth.txphook.databinding.ItemHookAppsBinding
import com.tokyonth.txphook.db.HookAppInfo
import com.tokyonth.txphook.utils.PackageUtils

class HookAppsAdapter(val context: Context) : RecyclerView.Adapter<HookAppsAdapter.ViewHolder>() {

    private var dataArr: MutableList<HookAppInfo>? = null

    private var click: ((Int, HookAppInfo) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(dataArr: MutableList<HookAppInfo>) {
        this.dataArr = dataArr
        notifyDataSetChanged()
    }

    fun setItemClick(click: (Int, HookAppInfo) -> Unit) {
        this.click = click
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHookAppsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataArr?.get(position)
        if (data != null && click != null) {
            holder.bind(context, data, click!!)
        }
    }

    override fun getItemCount(): Int {
        return dataArr?.size ?: 0
    }

    class ViewHolder(private val binding: ItemHookAppsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(
            context: Context,
            hookAppInfo: HookAppInfo,
            click: ((Int, HookAppInfo) -> Unit)
        ) {
            binding.run {
                itemTvName.text = hookAppInfo.config.appName
                itemTvVersion.text = PackageUtils.getVersionNameByPackageName(
                    context,
                    hookAppInfo.config.packageName
                )
                itemTvCurrVersion.append(hookAppInfo.config.appVersion)
                itemIvIcon.setImageDrawable(
                    PackageUtils.getAppIconByPackageName(
                        context,
                        hookAppInfo.config.packageName
                    )
                )
                itemTvRuleValue.text = "${hookAppInfo.rule.size} 条Hook规则"
            }

            binding.root.setOnClickListener {
                click.invoke(adapterPosition, hookAppInfo)
            }
        }

    }

}
