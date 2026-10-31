package com.example.mynotifications.presentation.feature.home.model

import com.example.mynotifications.data.database.NotificationItem
import com.example.mynotifications.data.extensions.toFormattedDate
import com.example.mynotifications.data.extensions.toFormattedHour
import java.time.ZoneId

private val knownApplications = mapOf(
    "com.instagram.android" to "Instagram",
    "com.whatsapp" to "WhatsApp",
)

data class NotificationUiModel(
    val uid: Int,
    val title: String,
    val message: String,
    val packageName: String,
    val appName: String,
    val formattedDate: String,
    val formattedHour: String,
    val iconRes: Int?,
)

fun NotificationItem.toUiModel(zone: ZoneId = ZoneId.systemDefault()) = NotificationUiModel(
    uid = uid,
    title = title,
    message = message,
    packageName = packageName,
    appName = knownApplications.getOrDefault(packageName, packageName),
    formattedDate = postTime.toFormattedDate(zone),
    formattedHour = postTime.toFormattedHour(zone),
    iconRes = iconRes,
)
