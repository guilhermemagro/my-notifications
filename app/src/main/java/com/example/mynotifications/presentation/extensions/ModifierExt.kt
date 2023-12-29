package com.example.mynotifications.presentation.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun Modifier.drawBottomLine(lineColor: Color): Modifier {
    return this.then(drawBehind {
        val strokeWidth = 1.dp.toPx()
        val lineYPosition = size.height - strokeWidth / 2

        drawLine(
            lineColor,
            Offset(0f, lineYPosition),
            Offset(size.width, lineYPosition),
            strokeWidth
        )
    })
}
