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
import com.tokyonth.txphook.utils.ktx.dp2px

class ProgressDialog(context: Context) : AlertDialog(context, R.style.Theme_TxpHook_Dialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dialogWindow: Window? = window
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.TOP)
            val lp: WindowManager.LayoutParams = dialogWindow.attributes
            val d = context.resources.displayMetrics
            lp.width = (d.widthPixels * 0.5).toInt()
            lp.y = 80.dp2px().toInt()
            dialogWindow.attributes = lp
        }
        setCancelable(false)
        initView()
    }

    private fun initView() {
        val binding = LayoutDialogProgressBinding.inflate(LayoutInflater.from(context))
        binding.root.setBackgroundResource(R.drawable.bg_elevation_white)
        setContentView(binding.root)
    }

}
