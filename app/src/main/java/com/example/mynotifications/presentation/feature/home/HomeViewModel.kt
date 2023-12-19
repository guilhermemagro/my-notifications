package com.example.mynotifications.presentation.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotifications.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {
    val notifications = repository.getAllNotifications()

    fun deleteAllNotifications() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}
