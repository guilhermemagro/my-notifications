package com.example.mynotifications.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mynotifications.presentation.annotations.ThemePreviews
import com.example.mynotifications.presentation.extensions.drawBottomLine

@Composable
fun NotificationItemHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    val lineColor = MaterialTheme.colorScheme.outline

    Text(
        text = title,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .drawBottomLine(lineColor = lineColor)
            .padding(8.dp)
    )
}

@ThemePreviews
@Composable
fun NotificationItemHeaderPreview() {
    MaterialTheme {
        NotificationItemHeader(title = "Wednesday, 29 dez 2023")
    }
}