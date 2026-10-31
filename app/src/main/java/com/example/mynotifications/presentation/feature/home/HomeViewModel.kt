package com.example.mynotifications.presentation.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mynotifications.data.repository.NotificationRepository
import com.example.mynotifications.presentation.feature.home.model.HomeListItem
import com.example.mynotifications.presentation.feature.home.model.toHomeListItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    val items: Flow<PagingData<HomeListItem>> = repository.getNotifications()
        .map { it.toHomeListItems() }
        .cachedIn(viewModelScope)

    fun deleteAllNotifications() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun deleteNotification(uid: Int) {
        viewModelScope.launch {
            repository.deleteById(uid)
        }
    }
}
