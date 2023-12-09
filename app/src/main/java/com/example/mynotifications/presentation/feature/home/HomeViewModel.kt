package com.example.mynotifications.presentation.feature.home

import androidx.lifecycle.ViewModel
import com.example.mynotifications.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

}