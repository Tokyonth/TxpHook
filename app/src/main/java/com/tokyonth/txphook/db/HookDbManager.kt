package com.tokyonth.txphook.db

import android.content.Context
import androidx.room.Room
import com.tokyonth.txphook.App
import com.tokyonth.txphook.Constants

class HookDbManager(context: Context) {

    companion object {

        private var hookDbManager: HookDbManager? = null

        val instance: HookDbManager by lazy {
            HookDbManager(App.context)
        }

        fun of(context: Context): HookDbManager {
            if (hookDbManager == null) {
                hookDbManager = HookDbManager(context)
            }
            return hookDbManager!!
        }

    }

    private var db: SQLDatabase = Room.databaseBuilder(
        context,
        SQLDatabase::class.java,
        Constants.DATABASE_TABLE_CONFIG_NAME
    ).build()

    fun getDataBase(): SQLDatabase {
        return db
    }

    fun getDao(): HookDao {
        return db.hookDao()
    }

}
