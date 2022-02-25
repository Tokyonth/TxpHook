package com.tokyonth.txphook.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tokyonth.txphook.Constants

@Entity(tableName = Constants.DATABASE_TABLE_NAME)
data class HookConfig(
    @PrimaryKey(autoGenerate = true)
    var number: Long = 0,
    val appName: String,
    val packageName: String,
    val appVersion: String,
    val hookAmount: String
)
