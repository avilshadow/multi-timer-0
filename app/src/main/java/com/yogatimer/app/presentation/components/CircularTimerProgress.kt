package com.yogatimer.app.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yogatimer.app.util.TimeFormatter

/**
 * Circular Timer Progress component with animated countdown.
 *
 * Design Specifications:
 * - Diameter: 280dp
 * - Stroke width: 12dp
 * - Background circle: surfaceVariant
 * - Progress circle: primary
 * - Cap: Round
 * - Direction: Clockwise from top
 * - Center text: countdown in MM:SS (displayLarge, 72sp, Light weight)
 *
 * Animation:
 * - Smooth 1-second transitions
 * - Easing: LinearEasing (consistent countdown)
 * - No bounce or overshoot
 *
 * @param remainingSeconds Seconds remaining in current timer
 * @param totalSeconds Total seconds for current timer
 * @param modifier Optional modifier
 */
@Composable
fun CircularTimerProgress(
    remainingSeconds: Int,
    totalSeconds: Int,
    modifier: Modifier = Modifier
) {
    // Animate progress smoothly over 1 second
    val progress = if (totalSeconds > 0) {
        1f - (remainingSeconds.toFloat() / totalSeconds.toFloat())
    } else {
        0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing
        ),
        label = "timer_progress"
    )

    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background circle (track)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.LightGray.copy(alpha = 0.3f),
                radius = size.minDimension / 2,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Progress circle (indicator)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = androidx.compose.ui.graphics.Color(0xFF6750A4), // Primary purple
                startAngle = -90f, // Start from top
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Countdown text in center
        Text(
            text = TimeFormatter.formatTime(remainingSeconds),
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 72.sp,
                fontWeight = FontWeight.Light
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
