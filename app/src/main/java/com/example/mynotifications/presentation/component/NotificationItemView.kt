package com.example.mynotifications.presentation.component

import android.content.Context
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mynotifications.presentation.extensions.getBitmapFromOrNull

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
    Card(modifier = modifier) {
        iconRes?.let { res ->
            val iconBitmap = context?.getBitmapFromOrNull(res, appName)
            iconBitmap?.let { bitmap ->
                Icon(bitmap = bitmap, contentDescription = null)
            }
        }
        Text(text = appName)
        Text(text = postTime)
        Text(text = title)
        Text(text = message)
    }
}
