package com.example.mynotifications.presentation.feature.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mynotifications.presentation.component.NotificationItemView
import com.example.mynotifications.presentation.theme.MyNotificationsTheme

// TODO - (?) How to get Hilt component in a Compose View?

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn {
            items(5) { index ->
                NotificationItemView(
                    title = "Title $index",
                    message = "Notification message of index $index"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyNotificationsTheme {
        HomeScreen()
    }
}
