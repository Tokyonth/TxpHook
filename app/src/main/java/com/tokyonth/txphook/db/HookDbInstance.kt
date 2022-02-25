package com.tokyonth.txphook.db

import androidx.room.Room
import com.tokyonth.txphook.App
import com.tokyonth.txphook.Constants

class HookDbInstance {

    companion object {

        val get: HookDbInstance by lazy {
            HookDbInstance()
        }

    }

    private var db: SQLDatabase = Room.databaseBuilder(
        App.context,
        SQLDatabase::class.java,
        Constants.DATABASE_TABLE_NAME
    ).build()

    fun getDataBase(): SQLDatabase {
        return db
    }

    fun getDao(): HookDao {
        return db.hookDao()
    }

}
