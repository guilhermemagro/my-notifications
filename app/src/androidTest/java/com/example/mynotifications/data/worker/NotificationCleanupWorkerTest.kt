package com.example.mynotifications.data.worker

import android.content.Context
import androidx.paging.PagingData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.mynotifications.data.database.NotificationItem
import com.example.mynotifications.data.repository.NotificationRepository
import com.example.mynotifications.data.utilities.NOTIFICATION_RETENTION
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
class NotificationCleanupWorkerTest {

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    private class FakeRepository(private val fail: Boolean = false) : NotificationRepository {
        var lastCutoff: Long? = null
        override fun getNotifications(): Flow<PagingData<NotificationItem>> = emptyFlow()
        override suspend fun insert(notificationItem: NotificationItem) = Unit
        override suspend fun deleteById(uid: Int) = Unit
        override suspend fun deleteAll() = Unit
        override suspend fun deleteOlderThan(minPostTime: Long): Int {
            if (fail) error("db failure")
            lastCutoff = minPostTime
            return 1
        }
    }

    private fun buildWorker(repository: NotificationRepository) =
        TestListenableWorkerBuilder<NotificationCleanupWorker>(context)
            .setWorkerFactory(object : WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters,
                ): ListenableWorker = NotificationCleanupWorker(appContext, workerParameters, repository)
            })
            .build()

    @Test
    fun doWork_deletesWithTwoDayCutoff_andSucceeds() = runTest {
        val repository = FakeRepository()

        val result = buildWorker(repository).doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        val expected = System.currentTimeMillis() - NOTIFICATION_RETENTION.inWholeMilliseconds
        val cutoff = requireNotNull(repository.lastCutoff)
        assertTrue(abs(expected - cutoff) < 5.seconds.inWholeMilliseconds)
    }

    @Test
    fun doWork_onFailure_retries() = runTest {
        val result = buildWorker(FakeRepository(fail = true)).doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun schedule_isIdempotent() {
        WorkManagerTestInitHelper.initializeTestWorkManager(context)

        repeat(3) { NotificationCleanupScheduler.schedule(context) }

        val infos = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork(NOTIFICATION_CLEANUP_WORK_NAME)
            .get()
        assertEquals(1, infos.size)
    }
}
