package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ServerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {
    @Query("SELECT * FROM servers ORDER BY isDefault DESC, id ASC")
    fun getAllServers(): Flow<List<ServerEntity>>

    @Query("SELECT * FROM servers WHERE id = :id LIMIT 1")
    suspend fun getServerById(id: Long): ServerEntity?

    @Query("SELECT * FROM servers WHERE isDefault = 1 LIMIT 1")
    fun getDefaultServer(): Flow<ServerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: ServerEntity): Long

    @Update
    suspend fun updateServer(server: ServerEntity)

    @Query("DELETE FROM servers WHERE id = :id")
    suspend fun deleteServer(id: Long)

    @Query("UPDATE servers SET isDefault = 0")
    suspend fun clearDefaultServer()

    @Query("UPDATE servers SET isDefault = 1 WHERE id = :id")
    suspend fun setDefaultServer(id: Long)

    @Query("UPDATE servers SET isOnline = :isOnline, lastPingMs = :pingMs WHERE id = :id")
    suspend fun updatePingStatus(id: Long, isOnline: Boolean, pingMs: Long)
}
