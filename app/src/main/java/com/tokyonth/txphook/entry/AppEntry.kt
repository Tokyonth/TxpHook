package com.tokyonth.txphook.entry

import android.graphics.drawable.Drawable

data class AppEntry(
    val appIcon: Drawable,
    val appName: String,
    val appVersion: String,
    val packageName: String
)
