package com.example.mynotifications.presentation.feature.home.model

import com.example.mynotifications.data.database.NotificationItem

data class NotificationCategory(
    val date: String,
    val notifications: List<NotificationItem>
)
