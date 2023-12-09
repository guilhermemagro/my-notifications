package com.example.mynotifications.data.repository

import com.example.mynotifications.data.database.NotificationDao
import com.example.mynotifications.data.database.NotificationItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface NotificationRepository {
    fun getAllNotifications(): Flow<List<NotificationItem>>
    suspend fun insert(notificationItem: NotificationItem)
    suspend fun delete(notificationItem: NotificationItem)
}

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {
    override fun getAllNotifications(): Flow<List<NotificationItem>> {
        return notificationDao.getAllNotifications()
    }

    override suspend fun insert(notificationItem: NotificationItem) {
        notificationDao.insert(notificationItem)
    }

    override suspend fun delete(notificationItem: NotificationItem) {
        notificationDao.delete(notificationItem)
    }
}
