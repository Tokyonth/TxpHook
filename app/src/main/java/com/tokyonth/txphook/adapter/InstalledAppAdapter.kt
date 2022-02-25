package com.tokyonth.txphook.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tokyonth.txphook.databinding.ItemInstalledAppsBinding
import com.tokyonth.txphook.entry.AppEntry

class InstalledAppAdapter : RecyclerView.Adapter<InstalledAppAdapter.ViewHolder>() {

    private var dataArr: MutableList<AppEntry>? = null

    private var click: ((Int, AppEntry) -> Unit)? = null

    private lateinit var groupMap: Map<Int, String>

    @SuppressLint("NotifyDataSetChanged")
    fun setData(dataArr: MutableList<AppEntry>) {
        this.dataArr = dataArr
        notifyDataSetChanged()
    }

    fun setItemClick(click: (Int, AppEntry) -> Unit) {
        this.click = click
    }

    fun setAppGroupMap(map: Map<Int, String>) {
        groupMap = map
    }

    fun getLetterPosition(letter: String): Int {
        for (s in groupMap) {
            if (s.value == letter) {
                return s.key
            }
        }
        return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemInstalledAppsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataArr?.get(position)
        if (data != null && click != null) {
            holder.bind(data, groupMap, click!!)
        }
    }

    override fun getItemCount(): Int {
        return dataArr?.size ?: 0
    }

    class ViewHolder(private val binding: ItemInstalledAppsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            appEntry: AppEntry,
            map: Map<Int, String>,
            click: (Int, AppEntry) -> Unit
        ) {
            binding.run {
                itemIvIcon.setImageDrawable(appEntry.appIcon)
                itemTvName.text = appEntry.appName
                itemTvVersion.text = appEntry.appVersion
            }

            if (map.containsKey(adapterPosition)) {
                binding.itemTvAppIndex.visibility = View.VISIBLE
                binding.itemTvAppIndex.text = map[adapterPosition]
            } else {
                binding.itemTvAppIndex.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                click.invoke(adapterPosition, appEntry)
            }
        }

    }

}
