package com.example.mynotifications.data.utilities

import kotlin.time.Duration.Companion.days

const val DATABASE_NAME = "mynotifications-db"

/** Logcat tag shared by the whole app. */
const val LOG_TAG = "MyNotifications"

/** Notifications older than this are hidden from the list and deleted by the cleanup routine. */
val NOTIFICATION_RETENTION = 2.days

/** Oldest post time (epoch millis) still within [NOTIFICATION_RETENTION], relative to [now]. */
fun retentionCutoff(now: Long = System.currentTimeMillis()): Long =
    now - NOTIFICATION_RETENTION.inWholeMilliseconds
