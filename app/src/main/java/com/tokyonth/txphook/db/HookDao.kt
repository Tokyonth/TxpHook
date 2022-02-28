package com.tokyonth.txphook.db

import androidx.room.*
import com.tokyonth.txphook.Constants

@Dao
interface HookDao {

    @Transaction
    @Query("SELECT * FROM ${Constants.DATABASE_TABLE_CONFIG_NAME}")
    suspend fun queryAllConfig(): List<HookAppInfo>

    @Transaction
    @Query("SELECT * FROM ${Constants.DATABASE_TABLE_CONFIG_NAME} where packageName = :pkgName")
    suspend fun getRulesByPkg(pkgName: String): HookAppInfo?

    @Insert
    suspend fun insertConfig(vararg hookConfigs: HookConfig): List<Long>

    @Insert
    suspend fun insertRule(vararg hookRule: HookRule): List<Long>

    @Delete
    suspend fun deleteConfig(hookConfig: HookConfig): Int

    @Delete
    suspend fun deleteRule(hookRule: HookRule): Int

    @Update
    suspend fun update(hookConfig: HookConfig): Int

}
