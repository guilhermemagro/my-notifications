package com.example.mynotifications.presentation.component

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissibleNotificationItemView(
    modifier: Modifier = Modifier,
    title: String = "",
    message: String = "",
    appName: String = "",
    postTime: String = "",
    iconRes: Int? = null,
    context: Context? = null,
    onDeleteNotification: (() -> (Unit))? = null,
) {
    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart) {
                onDeleteNotification?.invoke()
            }
            true
        }
    )

    SwipeToDismiss(
        modifier = modifier,
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        },
        dismissContent = {
            NotificationItemView(
                modifier = Modifier.fillMaxSize(),
                title = title,
                message = message,
                appName = appName,
                postTime = postTime,
                iconRes = iconRes,
                context = context,
            )
        }
    )
}
