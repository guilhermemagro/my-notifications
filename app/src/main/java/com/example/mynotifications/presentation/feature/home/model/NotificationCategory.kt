package com.example.mynotifications.presentation.feature.home.model

import com.example.mynotifications.data.database.NotificationItem

data class NotificationCategory(
    val name: String,
    val notifications: List<NotificationItem>
)
