package com.tokyonth.txphook.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [HookConfig::class, HookRule::class],
    exportSchema = false,
    version = 2
)
abstract class SQLDatabase : RoomDatabase() {

    abstract fun hookDao(): HookDao

}
