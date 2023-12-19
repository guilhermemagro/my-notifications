package com.example.mynotifications.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notificationitem ORDER BY uid ASC")
    fun getAllNotifications(): Flow<List<NotificationItem>>

    @Insert
    suspend fun insert(notificationItem: NotificationItem)

    @Delete
    suspend fun delete(notificationItem: NotificationItem)

    @Query("DELETE FROM notificationitem")
    suspend fun deleteAll()
}
