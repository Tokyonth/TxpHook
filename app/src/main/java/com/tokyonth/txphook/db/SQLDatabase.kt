package com.tokyonth.txphook.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HookConfig::class], version = 2)
abstract class SQLDatabase : RoomDatabase() {

    abstract fun hookDao(): HookDao

}
