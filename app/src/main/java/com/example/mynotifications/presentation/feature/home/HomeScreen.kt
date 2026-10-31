package com.example.mynotifications.presentation.feature.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.mynotifications.presentation.component.DismissibleNotificationItemView
import com.example.mynotifications.presentation.component.GoToEndFloatingActionButton
import com.example.mynotifications.presentation.component.NotificationItemHeader
import com.example.mynotifications.presentation.feature.home.model.HomeListItem
import com.example.mynotifications.presentation.theme.MyNotificationsTheme
import kotlinx.coroutines.launch

private const val NOTIFICATION_LISTENER_SETTINGS =
    "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
private const val NOTIFICATION_BUTTON_TEXT = "Open Notifications Listener settings"
private const val DELETE_BUTTON_TEXT = "Delete all notifications"
private val PLACEHOLDER_HEIGHT = 72.dp

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val items = viewModel.items.collectAsLazyPagingItems()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val intent = Intent(NOTIFICATION_LISTENER_SETTINGS)
    val context = LocalContext.current
    // Index 0 of the LazyColumn holds the buttons, so paging item i is at list index i + 1.
    val pinnedDate by remember(items) {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo
                .asSequence()
                .map { it.index - 1 }
                .filter { it in 0 until items.itemCount }
                .firstNotNullOfOrNull { index ->
                    when (val item = items.peek(index)) {
                        is HomeListItem.DayHeader -> item.date
                        is HomeListItem.Notification -> item.model.formattedDate
                        null -> null
                    }
                }
                ?.takeIf { listState.firstVisibleItemIndex > 0 }
        }
    }
    Scaffold(
        floatingActionButton = {
            GoToEndFloatingActionButton(onClick = {
                coroutineScope.launch {
                    // scrollToItem (not animate) so paging jumps straight to the last page.
                    listState.scrollToItem(index = listState.layoutInfo.totalItemsCount - 1)
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
                Box(modifier = Modifier.fillMaxSize()) {
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
                        // peek() does not trigger loading; items[index] inside the content does.
                        for (index in 0 until items.itemCount) {
                            when (val peeked = items.peek(index)) {
                                is HomeListItem.DayHeader -> item(key = peeked.key) {
                                    val header = items[index] as? HomeListItem.DayHeader ?: peeked
                                    NotificationItemHeader(title = header.date)
                                }

                                is HomeListItem.Notification -> item(key = peeked.model.uid) {
                                    val notification =
                                        (items[index] as? HomeListItem.Notification ?: peeked).model
                                    DismissibleNotificationItemView(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .animateItem(),
                                        title = notification.title,
                                        message = notification.message,
                                        appName = notification.appName,
                                        packageName = notification.packageName,
                                        postTime = notification.formattedHour,
                                        iconRes = notification.iconRes,
                                        context = context,
                                        onDeleteNotification = {
                                            viewModel.deleteNotification(notification.uid)
                                        },
                                    )
                                }

                                null -> item {
                                    items[index]
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(PLACEHOLDER_HEIGHT)
                                    )
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.size(64.dp))
                        }
                    }
                    // Pinned day header drawn over the list. LazyColumn's stickyHeader can't be used:
                    // after "go to end" jumps, the day's header sits in a not-yet-loaded
                    // (placeholder) region, so it would never be registered as sticky.
                    pinnedDate?.let { date ->
                        // Same horizontal inset as the LazyColumn's contentPadding.
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        ) {
                            NotificationItemHeader(title = date)
                        }
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
