package com.example.mynotifications.presentation.component

import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NotificationItemView(
    modifier: Modifier = Modifier,
    title: String = "",
    message: String = "",
    packageName: String = "",
    postTime: String = "",
) {
    Card(modifier = modifier) {
        // TODO - Add icon image
        Text(text = title)
        Text(text = message)
        Text(text = packageName)
        Text(text = postTime)
    }
}
