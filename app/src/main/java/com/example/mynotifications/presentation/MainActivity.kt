package com.example.mynotifications.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mynotifications.presentation.feature.home.HomeScreen
import com.example.mynotifications.presentation.theme.MyNotificationsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyNotificationsTheme {
                HomeScreen()
            }
        }
    }
}
