package com.tokyonth.txphook.db

import androidx.room.Embedded
import androidx.room.Relation

data class HookAppInfo(
    @Embedded
    val config: HookConfig,

    @Relation(
        parentColumn = "packageName",
        entityColumn = "pkgName"
    )
    val rule: List<HookRule>
)
