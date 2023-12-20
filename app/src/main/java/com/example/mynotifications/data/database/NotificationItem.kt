package com.example.mynotifications.data.database

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.mynotifications.data.extensions.toFormattedDate
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

private val oneSecond = 1.seconds.inWholeMilliseconds

private val knownApplications = mapOf(
    "com.instagram.android" to "Instagram",
    "com.whatsapp" to "WhatsApp",
)

@Entity
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    val title: String,
    val message: String,
    val packageName: String,
    val postTime: Long,
    val iconRes: Int?,
    val category: String,
) {
    @Ignore
    val appName = knownApplications.getOrDefault(packageName, packageName)

    @Ignore
    val formattedPostTime = postTime.toFormattedDate()

    constructor(
        title: String,
        message: String,
        packageName: String,
        postTime: Long,
        iconRes: Int?,
        category: String,
    ) : this(0, title, message, packageName, postTime, iconRes, category)

    override fun equals(other: Any?): Boolean {
        return ((other != null)
                && (other is NotificationItem)
                && (other.title == title)
                && (other.message == message)
                && (other.packageName == packageName))
                && (abs(other.postTime - postTime) < oneSecond)
                && (other.iconRes == iconRes)
    }

    override fun hashCode(): Int {
        var result = uid
        result = 31 * result + title.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + packageName.hashCode()
        result = 31 * result + postTime.hashCode()
        result = 31 * result + (iconRes ?: 0)
        return result
    }
}
