package com.example.mynotifications.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mynotifications.data.repository.NotificationRepository
import com.example.mynotifications.data.utilities.LOG_TAG
import com.example.mynotifications.data.utilities.retentionCutoff
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/** Deletes notifications older than [com.example.mynotifications.data.utilities.NOTIFICATION_RETENTION]. */
@HiltWorker
class NotificationCleanupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: NotificationRepository,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val deleted = repository.deleteOlderThan(retentionCutoff())
            Log.d(LOG_TAG, "NotificationCleanupWorker deleted $deleted expired notifications")
            Result.success()
        } catch (e: Exception) {
            Log.e(LOG_TAG, "NotificationCleanupWorker failed", e)
            Result.retry()
        }
    }
}
