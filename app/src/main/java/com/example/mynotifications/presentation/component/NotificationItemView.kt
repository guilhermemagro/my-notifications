package com.example.mynotifications.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NotificationItemView(
    modifier: Modifier = Modifier,
    title: String = "",
    message: String = "",
) {
    Column {
        // TODO - Add icon image
        Text(text = title)
        Text(text = message)
    }
}
