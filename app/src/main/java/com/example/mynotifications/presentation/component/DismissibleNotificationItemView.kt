package com.example.mynotifications.presentation.component

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mynotifications.presentation.theme.MyNotificationsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissibleNotificationItemView(
    modifier: Modifier = Modifier,
    title: String = "",
    message: String = "",
    appName: String = "",
    postTime: String = "",
    iconRes: Int? = null,
    category: String = "",
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
        background = { SwipeBackground() },
        dismissContent = {
            NotificationItemView(
                modifier = Modifier.fillMaxSize(),
                title = title,
                message = message,
                appName = appName,
                postTime = postTime,
                iconRes = iconRes,
                category = category,
                context = context,
            )
        }
    )
}

@Composable
fun RowScope.SwipeBackground(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(AbsoluteRoundedCornerShape(12.0.dp))
            .background(Color.Red)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.background
        )
    }
}

@Preview
@Composable
fun DismissibleNotificationItemViewPreview() {
    MyNotificationsTheme {
        DismissibleNotificationItemView(
            title = "Titulo teste",
            message = "Mensagem teste teste brown fox",
            appName = "Instagram",
            postTime = "10/10/2024 - 15:30:20"
        )
    }
}
