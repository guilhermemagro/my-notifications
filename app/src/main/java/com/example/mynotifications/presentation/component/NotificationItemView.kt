package com.example.mynotifications.presentation.component

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mynotifications.presentation.annotations.ThemePreviews
import com.example.mynotifications.presentation.extensions.getBitmapFromOrNull
import com.example.mynotifications.presentation.theme.MyNotificationsTheme

@Composable
fun NotificationItemView(
    modifier: Modifier = Modifier,
    title: String = "",
    message: String = "",
    appName: String = "",
    postTime: String = "",
    iconRes: Int? = null,
    context: Context? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
            )
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Row {
            iconRes?.let { res ->
                val iconBitmap = context?.getBitmapFromOrNull(res, appName)
                iconBitmap?.let { bitmap ->
                    Icon(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 4.dp),
                        bitmap = bitmap,
                        contentDescription = null
                    )
                }
            }
            Text(
                text = appName,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = postTime,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontWeight = W500,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@ThemePreviews
@Composable
fun NotificationItemViewPreview() {
    MyNotificationsTheme {
        NotificationItemView(
            title = "Titulo teste",
            message = "Mensagem teste teste brown fox",
            appName = "Instagram",
            postTime = "15:30:20",
        )
    }
}
