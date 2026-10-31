package com.example.mynotifications.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.mynotifications.data.database.NotificationDao
import com.example.mynotifications.data.database.NotificationItem
import com.example.mynotifications.data.utilities.retentionCutoff
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

private const val PAGE_SIZE = 50

interface NotificationRepository {
    /** Notifications within the retention period, oldest first, loaded in pages. */
    fun getNotifications(): Flow<PagingData<NotificationItem>>
    suspend fun insert(notificationItem: NotificationItem)
    suspend fun deleteById(uid: Int)
    suspend fun deleteAll()
    suspend fun deleteOlderThan(minPostTime: Long): Int
}

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {

    override fun getNotifications(): Flow<PagingData<NotificationItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = true,
                // Lets "go to end" jump straight to the last page instead of loading every page.
                jumpThreshold = PAGE_SIZE * 3,
            ),
            // Room invalidates the source on every table change, so the cutoff is refreshed often.
            pagingSourceFactory = { notificationDao.pagingSource(retentionCutoff()) },
        ).flow
    }

    override suspend fun insert(notificationItem: NotificationItem) {
        notificationDao.insert(notificationItem)
    }

    override suspend fun deleteById(uid: Int) {
        notificationDao.deleteById(uid)
    }

    override suspend fun deleteAll() {
        notificationDao.deleteAll()
    }

    override suspend fun deleteOlderThan(minPostTime: Long): Int {
        return notificationDao.deleteOlderThan(minPostTime)
    }
}
