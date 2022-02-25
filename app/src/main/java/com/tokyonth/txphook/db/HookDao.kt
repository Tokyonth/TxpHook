package com.tokyonth.txphook.db

import androidx.room.*
import com.tokyonth.txphook.Constants

@Dao
interface HookDao {

    @Query("SELECT * FROM ${Constants.DATABASE_TABLE_NAME}")
    suspend fun queryAll(): List<HookConfig>

    @Insert
    suspend fun insert(vararg hookConfigs: HookConfig): List<Long>

    @Delete
    suspend fun delete(hookConfig: HookConfig): Int

    @Update
    suspend fun update(hookConfig: HookConfig): Int

}
