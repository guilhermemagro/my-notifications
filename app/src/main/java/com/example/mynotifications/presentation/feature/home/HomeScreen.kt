package com.example.mynotifications.presentation.feature.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mynotifications.presentation.component.DismissibleNotificationItemView
import com.example.mynotifications.presentation.component.GoToEndFloatingActionButton
import com.example.mynotifications.presentation.component.NotificationItemHeader
import com.example.mynotifications.presentation.theme.MyNotificationsTheme
import kotlinx.coroutines.launch

private const val NOTIFICATION_LISTENER_SETTINGS =
    "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
private const val NOTIFICATION_BUTTON_TEXT = "Open Notifications Listener settings"
private const val DELETE_BUTTON_TEXT = "Delete all notifications"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val notificationsCategories by viewModel.notificationCategories.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val intent = Intent(NOTIFICATION_LISTENER_SETTINGS)
    val context = LocalContext.current
    Scaffold(
        floatingActionButton = {
            GoToEndFloatingActionButton(onClick = {
                coroutineScope.launch {
                    listState.animateScrollToItem(index = listState.layoutInfo.totalItemsCount - 1)
                }
            })
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = MaterialTheme.colorScheme.background
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        Column {
                            Button(
                                onClick = { openNotificationListenerSettings(context, intent) }
                            ) {
                                Text(text = NOTIFICATION_BUTTON_TEXT)
                            }
                            Button(
                                onClick = viewModel::deleteAllNotifications
                            ) {
                                Text(text = DELETE_BUTTON_TEXT)
                            }
                        }
                    }
                    notificationsCategories.forEach { category ->
                        stickyHeader {
                            NotificationItemHeader(title = category.name)
                        }
                        items(
                            items = category.notifications,
                            key = { item -> item.uid }
                        ) { notification ->
                            with(notification) {
                                DismissibleNotificationItemView(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateItemPlacement(),
                                    title = title,
                                    message = message,
                                    appName = appName,
                                    postTime = formattedHour,
                                    iconRes = iconRes,
                                    context = context,
                                    onDeleteNotification = { viewModel.deleteNotification(this) },
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.size(64.dp))
                    }
                }
            }
        }
    )
}

private fun openNotificationListenerSettings(context: Context, intent: Intent) {
    startActivity(context, intent, null)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyNotificationsTheme {
        HomeScreen()
    }
}
