package com.tokyonth.txphook.db

import android.content.Context
import androidx.room.Room
import com.tokyonth.txphook.App
import com.tokyonth.txphook.Constants

class HookDbManager(context: Context) {

    companion object {

        private var instance: HookDbManager? = null

        val get: HookDbManager by lazy {
            HookDbManager(App.context)
        }

        fun of(context: Context): HookDbManager {
            if (instance == null) {
                instance = HookDbManager(context)
            }
            return instance!!
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
