package com.tokyonth.txphook.entity

import android.graphics.drawable.Drawable

data class AppEntity(
    val appIcon: Drawable,
    val appName: String,
    val appVersion: String,
    val packageName: String
)
