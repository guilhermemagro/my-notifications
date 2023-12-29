package com.example.mynotifications.presentation.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotifications.data.database.NotificationItem
import com.example.mynotifications.data.repository.NotificationRepository
import com.example.mynotifications.presentation.feature.home.model.NotificationCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {
    val notificationCategories = repository.getAllNotifications().map { notifications ->
        notifications
            .groupBy { it.formattedDate }
            .map {
                NotificationCategory(
                    name = it.key,
                    notifications = it.value
                )
            }
    }

    fun deleteAllNotifications() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun deleteNotification(notification: NotificationItem) {
        viewModelScope.launch {
            repository.delete(notification)
        }
    }
}
