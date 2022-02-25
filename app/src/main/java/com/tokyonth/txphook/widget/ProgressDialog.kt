package com.tokyonth.txphook.widget

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.tokyonth.txphook.R
import com.tokyonth.txphook.databinding.LayoutDialogProgressBinding

class ProgressDialog(context: Context) : AlertDialog(context, R.style.Theme_TxpHook_Dialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dialogWindow: Window? = window
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.CENTER)
            val lp: WindowManager.LayoutParams = dialogWindow.attributes
            val d = context.resources.displayMetrics
            lp.width = (d.widthPixels * 0.5).toInt()
            dialogWindow.attributes = lp
        }
        initView()
    }

    private fun initView() {
        val binding = LayoutDialogProgressBinding.inflate(LayoutInflater.from(context))
        binding.root.setBackgroundResource(R.drawable.bg_dialog_round)
        setContentView(binding.root)
    }

}
