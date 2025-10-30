package com.yogatimer.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yogatimer.app.util.TimeFormatter

/**
 * Section Repeat Progress Bar with dual-layer visualization.
 *
 * Design Specifications:
 * - Width: 80% of screen width
 * - Height: 12dp
 * - Corner radius: 6dp
 * - Two layers:
 *   Layer 1 (bottom): Total progress - primaryContainer (darker)
 *   Layer 2 (top): Current repeat progress - primary (lighter)
 * - Label: "Repeat X of Y" (titleMedium, Medium weight)
 * - Margin bottom: 8dp
 *
 * @param currentRepeat Current repeat number (1-indexed)
 * @param totalRepeats Total number of repeats
 * @param currentTimerIndex Current timer index within repeat (0-indexed)
 * @param totalTimers Total timers in section
 * @param modifier Optional modifier
 */
@Composable
fun SectionRepeatProgress(
    currentRepeat: Int,
    totalRepeats: Int,
    currentTimerIndex: Int,
    totalTimers: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Repeat $currentRepeat of $totalRepeats",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(12.dp)
        ) {
            // Layer 1: Total progress (darker background)
            // Shows completed repeats
            val totalProgress = if (totalRepeats > 0) {
                (currentRepeat - 1).toFloat() / totalRepeats.toFloat()
            } else {
                0f
            }

            LinearProgressIndicator(
                progress = totalProgress,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(6.dp)),
                color = MaterialTheme.colorScheme.primaryContainer,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Layer 2: Current repeat progress (lighter overlay)
            // Shows progress within current repeat
            val currentProgress = if (totalTimers > 0) {
                currentTimerIndex.toFloat() / totalTimers.toFloat()
            } else {
                0f
            }

            val incrementProgress = if (totalRepeats > 0) {
                currentProgress / totalRepeats.toFloat()
            } else {
                0f
            }

            val combinedProgress = totalProgress + incrementProgress

            LinearProgressIndicator(
                progress = combinedProgress,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(6.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent
            )
        }
    }
}

/**
 * Overall Workout Progress Bar showing exercise count and time.
 *
 * Design Specifications:
 * - Width: 80% of screen width
 * - Height: 8dp
 * - Corner radius: 4dp
 * - Color: secondary
 * - Track color: surfaceVariant
 * - Label row:
 *   - Left: "Exercise X of Y" (bodyMedium)
 *   - Right: "elapsed / total" (bodyMedium)
 *   - Margin bottom: 8dp
 *
 * @param currentExercise Current exercise number (1-indexed)
 * @param totalExercises Total number of exercises
 * @param elapsedSeconds Total elapsed time in seconds
 * @param totalSeconds Total workout duration in seconds
 * @param modifier Optional modifier
 */
@Composable
fun OverallWorkoutProgress(
    currentExercise: Int,
    totalExercises: Int,
    elapsedSeconds: Int,
    totalSeconds: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(0.8f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Label row with exercise count and time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Exercise $currentExercise of $totalExercises",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${TimeFormatter.formatTime(elapsedSeconds)} / ${TimeFormatter.formatTime(totalSeconds)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress bar
        val progress = if (totalExercises > 0) {
            currentExercise.toFloat() / totalExercises.toFloat()
        } else {
            0f
        }

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
