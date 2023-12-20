package com.example.mynotifications.presentation.feature.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mynotifications.presentation.component.DismissibleNotificationItemView
import com.example.mynotifications.presentation.theme.MyNotificationsTheme

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
    val notifications by viewModel.notifications.collectAsState(initial = emptyList())
    val intent = Intent(NOTIFICATION_LISTENER_SETTINGS)
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
            items(
                items = notifications,
                key = { item -> item.uid }
            ) { notification ->
                with(notification) {
                    DismissibleNotificationItemView(
                        modifier = Modifier.fillMaxWidth().animateItemPlacement(),
                        title = title,
                        message = message,
                        appName = appName,
                        postTime = formattedPostTime,
                        iconRes = iconRes,
                        category = category,
                        context = context,
                        onDeleteNotification = { viewModel.deleteNotification(this) },
                    )
                }
            }
        }
    }
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
