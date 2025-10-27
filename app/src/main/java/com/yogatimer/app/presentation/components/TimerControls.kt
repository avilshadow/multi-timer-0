package com.yogatimer.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Timer Control Buttons for Active Timer screen.
 *
 * Design Specifications:
 * - Size: 64dp × 64dp touch target
 * - Icon size: 48dp × 48dp
 * - Spacing between: 24dp
 * - Color: onSurface
 * - Background: secondaryContainer (Pause/Skip), errorContainer (Stop)
 * - Ripple: bounded
 * - Elevation on press: 2dp → 4dp
 *
 * Button Order: Pause/Resume | Skip | Stop
 *
 * @param isRunning Whether timer is currently running
 * @param onPauseResume Callback when pause/resume is clicked
 * @param onSkip Callback when skip is clicked
 * @param onStop Callback when stop is clicked
 * @param modifier Optional modifier
 */
@Composable
fun TimerControls(
    isRunning: Boolean,
    onPauseResume: () -> Unit,
    onSkip: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pause/Resume button
        FilledIconButton(
            onClick = onPauseResume,
            modifier = Modifier.size(64.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Icon(
                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isRunning) "Pause" else "Resume",
                modifier = Modifier.size(48.dp)
            )
        }

        // Skip button
        FilledIconButton(
            onClick = onSkip,
            modifier = Modifier.size(64.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Skip",
                modifier = Modifier.size(48.dp)
            )
        }

        // Stop button
        FilledIconButton(
            onClick = onStop,
            modifier = Modifier.size(64.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "Stop",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}
