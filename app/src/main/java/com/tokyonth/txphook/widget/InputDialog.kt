package com.tokyonth.txphook.widget

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tokyonth.txphook.databinding.LayoutDialogInputBinding

class InputDialog(context: Context) : MaterialAlertDialogBuilder(context) {

    private val binding = LayoutDialogInputBinding.inflate(LayoutInflater.from(context))

    fun of(confirm: (String) -> Unit): InputDialog {
        setView(binding.root)
        setTitle("提示")
        setNegativeButton("取消", null)
        setPositiveButton("确定") { _, _ ->
            val name = binding.etHookName.text
            if (name.isNullOrEmpty()) {
                Toast.makeText(context, "不可为空!", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            confirm.invoke(name.toString())
        }
        return this
    }

}
