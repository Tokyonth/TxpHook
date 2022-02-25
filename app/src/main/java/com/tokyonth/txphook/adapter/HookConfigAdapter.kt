package com.tokyonth.txphook.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tokyonth.txphook.R
import com.tokyonth.txphook.databinding.ItemHookConfigBinding
import com.tokyonth.txphook.entry.HookInfoEntry
import com.tokyonth.txphook.utils.json.HookConfigManager
import com.tokyonth.txphook.utils.ktx.visibleOrGone

class HookConfigAdapter(context: Context) : RecyclerView.Adapter<HookConfigAdapter.ViewHolder>() {

    private var dataArr: MutableList<HookInfoEntry> = ArrayList()

    private var btnClick: ((String, Int, Int) -> Unit)? = null

    private var arrayAdapter: ArrayAdapter<*>

    init {
        val items = context.resources.getStringArray(R.array.HookDataType)
        arrayAdapter = ArrayAdapter(context, R.layout.item_list_array, items)
    }

    fun setBtnClick(btnClick: (String, Int, Int) -> Unit) {
        this.btnClick = btnClick
    }

    fun saveAll(tips: (String) -> Unit) {
        var ignore = ""
        val tempArr: MutableList<HookInfoEntry> = ArrayList()
        dataArr.forEach {
            if (it.methodName.isNullOrEmpty()
                && it.classPath.isNullOrEmpty()
                && it.resultVale != null
            ) {
                tempArr.add(it)
            } else {
                ignore += "${it.hookName}, \n"
            }
        }

        tips.invoke(ignore)
        if (tempArr.isNullOrEmpty())
            return
        HookConfigManager.getInstance().saveAllHookConfigs(tempArr)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(dataArr: MutableList<HookInfoEntry>) {
        this.dataArr.addAll(dataArr)
        notifyDataSetChanged()
    }

    fun getData(): MutableList<HookInfoEntry> {
        return dataArr
    }

    fun addSimpleData(pkgName: String, name: String) {
        HookInfoEntry().apply {
            hookName = name
            packageName = pkgName
            methodName = ""
            classPath = ""
            resultVale = ""
            dataArr.add(this)
            notifyItemInserted(dataArr.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHookConfigBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataArr[position]
        holder.bind(data, btnClick!!)
        holder.initExposedDrop(arrayAdapter)
    }

    override fun getItemCount(): Int {
        return dataArr.size
    }

    class ViewHolder(private val binding: ItemHookConfigBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(hookInfoEntry: HookInfoEntry, btnClick: (String, Int, Int) -> Unit) {
            binding.run {
                itemHookName.text = hookInfoEntry.hookName
                etHookPath.setText(hookInfoEntry.classPath)
                etHookResult.setText(hookInfoEntry.resultVale.toString())
                etHookMethodName.setText(hookInfoEntry.methodName)
            }

            binding.btnSaveHook.setOnClickListener {
                val status = fillConfigAndSave(hookInfoEntry)
                btnClick.invoke(status, adapterPosition, 1)
            }

            binding.btnDelHook.setOnClickListener {
                HookConfigManager.getInstance()
                    .removeHookConfig(hookInfoEntry.packageName, adapterPosition)
                btnClick.invoke("删除成功!", adapterPosition, 0)
            }

            binding.llHookMsg.setOnClickListener {
                binding.llHookConfig.visibility.let { vis ->
                    (vis == View.GONE).let {
                        binding.itemIvArrow.animate().rotation(if (it) 0F else -90F).start()
                        binding.llHookConfig.visibleOrGone(it)
                    }
                }
            }
        }

        fun initExposedDrop(adapter: ArrayAdapter<*>) {
            binding.itemExposedDrop.apply {
                setAdapter(adapter)
                keyListener = null
                ellipsize = TextUtils.TruncateAt.END
            }
        }

        private fun fillConfigAndSave(hookInfoEntry: HookInfoEntry): String {
            val methodName = binding.etHookMethodName.text.toString()
            val classPath = binding.etHookPath.text.toString()
            val resultVale = binding.etHookResult.text.toString()
            return if (methodName.isEmpty()
                && classPath.isEmpty()
                && resultVale.isEmpty()
            ) {
                "Rule不完整!"
            } else {
                hookInfoEntry.methodName = methodName
                hookInfoEntry.classPath = classPath
                hookInfoEntry.resultVale = resultVale
                HookConfigManager.getInstance().saveHookConfig(hookInfoEntry)
                "保存成功!"
            }
        }

    }

}
