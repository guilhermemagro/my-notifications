package com.example.mynotifications.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

private val oneSecond = 1.seconds.inWholeMilliseconds

@Entity
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    val title: String,
    val message: String,
    val packageName: String,
    val postTime: Long,
) {
    constructor(
        title: String,
        message: String,
        packageName: String,
        postTime: Long
    ) : this(0, title, message, packageName, postTime)

    override fun equals(other: Any?): Boolean {
        return ((other != null)
                && (other is NotificationItem)
                && (other.title == title)
                && (other.message == message)
                && (other.packageName == packageName))
                && (abs(other.postTime - postTime) < oneSecond)
    }

    override fun hashCode(): Int {
        var result = uid
        result = 31 * result + title.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + packageName.hashCode()
        result = 31 * result + postTime.hashCode()
        return result
    }
}
