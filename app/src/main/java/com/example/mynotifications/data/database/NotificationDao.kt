package com.example.mynotifications.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notificationitem WHERE postTime >= :minPostTime ORDER BY uid ASC")
    fun pagingSource(minPostTime: Long): PagingSource<Int, NotificationItem>

    @Insert
    suspend fun insert(notificationItem: NotificationItem)

    @Query("DELETE FROM notificationitem WHERE uid = :uid")
    suspend fun deleteById(uid: Int)

    @Query("DELETE FROM notificationitem")
    suspend fun deleteAll()

    /** Deletes every notification posted before [minPostTime]. Returns the number of rows deleted. */
    @Query("DELETE FROM notificationitem WHERE postTime < :minPostTime")
    suspend fun deleteOlderThan(minPostTime: Long): Int
}
