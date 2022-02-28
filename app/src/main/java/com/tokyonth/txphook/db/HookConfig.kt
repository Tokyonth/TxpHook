package com.tokyonth.txphook.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tokyonth.txphook.Constants

@Entity(tableName = Constants.DATABASE_TABLE_CONFIG_NAME)
data class HookConfig(
    @PrimaryKey(autoGenerate = true)
    val configId: Long = 0,
    val appName: String,
    val packageName: String,
    val appVersion: String,
)
