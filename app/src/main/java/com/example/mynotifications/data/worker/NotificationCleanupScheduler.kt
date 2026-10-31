package com.example.mynotifications.data.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

const val NOTIFICATION_CLEANUP_WORK_NAME = "notification-cleanup"
private const val CLEANUP_INTERVAL_HOURS = 6L

object NotificationCleanupScheduler {

    /** Schedules the periodic cleanup. Idempotent: an existing schedule is kept as is. */
    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<NotificationCleanupWorker>(
            CLEANUP_INTERVAL_HOURS, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            NOTIFICATION_CLEANUP_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }
}
