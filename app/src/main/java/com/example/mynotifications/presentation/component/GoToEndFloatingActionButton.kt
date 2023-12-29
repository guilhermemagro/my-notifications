package com.example.mynotifications.presentation.component

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate

@Composable
fun GoToEndFloatingActionButton(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = { onClick() },
        shape = CircleShape,
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.rotate(90f)
        )
    }
}
