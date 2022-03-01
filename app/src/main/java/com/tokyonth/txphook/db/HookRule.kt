package com.tokyonth.txphook.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tokyonth.txphook.Constants

@Entity(tableName = Constants.DATABASE_TABLE_RULE_NAME)
data class HookRule(
    @PrimaryKey(autoGenerate = true)
    val ruleId: Long = 0,
    var enableHook: Boolean,
    var pkgName: String,
    var hookName: String,
    var classPath: String,
    var methodName: String,
    var resultVale: String,
    var valueType: Int
)
