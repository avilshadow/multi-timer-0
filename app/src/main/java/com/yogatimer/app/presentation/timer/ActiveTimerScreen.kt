package com.yogatimer.app.presentation.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yogatimer.app.R
import com.yogatimer.app.domain.model.FlattenedTimer
import com.yogatimer.app.domain.model.TimerState
import com.yogatimer.app.util.TimeFormatter

/**
 * Active Timer Screen with workout execution and timer list.
 *
 * Features:
 * - Shows list of all timers with progress
 * - Start button when workout is idle
 * - Click current timer to pause/unpause
 * - Click other timer to jump to it
 * - Visual indicators for completed/current/upcoming timers
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveTimerScreen(
    onBack: () -> Unit,
    viewModel: ActiveTimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val timerState by viewModel.timerState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    // Handle navigation
    LaunchedEffect(uiState.shouldNavigateBack) {
        if (uiState.shouldNavigateBack) {
            onBack()
            viewModel.onNavigationHandled()
        }
    }

    // Show error messages
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.dismissError()
        }
    }

    // Auto-scroll to current timer
    LaunchedEffect(timerState) {
        if (timerState is TimerState.Running || timerState is TimerState.Paused) {
            val currentIndex = when (timerState) {
                is TimerState.Running -> (timerState as TimerState.Running).overallProgress.currentTimerGlobal - 1
                is TimerState.Paused -> (timerState as TimerState.Paused).overallProgress.currentTimerGlobal - 1
                else -> 0
            }
            if (currentIndex >= 0) {
                listState.animateScrollToItem(currentIndex)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.workout?.name ?: stringResource(R.string.nav_active_timer),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (timerState.isActive()) {
                            viewModel.onStopRequest()
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                timerState is TimerState.Completed -> {
                    WorkoutCompletedContent(
                        workoutName = uiState.workout?.name ?: "",
                        onDone = onBack,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    // Show timer list with controls
                    TimerListContent(
                        workout = uiState.workout,
                        timerState = timerState,
                        listState = listState,
                        onStart = viewModel::onStart,
                        onPauseResume = viewModel::onPauseResume,
                        onJumpToTimer = viewModel::onJumpToTimer
                    )
                }
            }
        }
    }

    // Stop confirmation dialog
    if (uiState.showStopConfirmation) {
        StopConfirmationDialog(
            onConfirm = { viewModel.confirmStop() },
            onDismiss = { viewModel.cancelStop() }
        )
    }
}

@Composable
private fun TimerListContent(
    workout: com.yogatimer.app.domain.model.Workout?,
    timerState: TimerState,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onStart: () -> Unit,
    onPauseResume: () -> Unit,
    onJumpToTimer: (Int) -> Unit
) {
    if (workout == null) return

    val flattenedTimers = workout.getAllTimersFlattened()
    val currentTimerIndex = when (timerState) {
        is TimerState.Running -> timerState.overallProgress.currentTimerGlobal - 1
        is TimerState.Paused -> timerState.overallProgress.currentTimerGlobal - 1
        else -> -1
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Start button (when Idle)
        if (timerState is TimerState.Idle) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onStart,
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Workout")
                }
            }
        }

        // Timer list
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(
                items = flattenedTimers,
                key = { index, _ -> index }
            ) { index, flattenedTimer ->
                TimerListItem(
                    flattenedTimer = flattenedTimer,
                    index = index,
                    isCurrent = index == currentTimerIndex,
                    isCompleted = index < currentTimerIndex,
                    timerState = timerState,
                    onClick = {
                        if (index == currentTimerIndex && timerState.isActive()) {
                            // Click on current timer = pause/unpause
                            onPauseResume()
                        } else if (timerState.isActive() || timerState is TimerState.Idle) {
                            // Click on other timer = jump to it
                            onJumpToTimer(index)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun TimerListItem(
    flattenedTimer: FlattenedTimer,
    index: Int,
    isCurrent: Boolean,
    isCompleted: Boolean,
    timerState: TimerState,
    onClick: () -> Unit
) {
    val timer = flattenedTimer.timer
    val section = flattenedTimer.section

    // Calculate progress for current timer
    val progress = if (isCurrent && (timerState is TimerState.Running || timerState is TimerState.Paused)) {
        val remaining = when (timerState) {
            is TimerState.Running -> timerState.remainingSeconds
            is TimerState.Paused -> timerState.remainingSeconds
            else -> timer.durationSeconds
        }
        1f - (remaining.toFloat() / timer.durationSeconds.toFloat())
    } else if (isCompleted) {
        1f
    } else {
        0f
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isCurrent -> MaterialTheme.colorScheme.primaryContainer
                isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrent) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> MaterialTheme.colorScheme.primary
                            isCurrent -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isCompleted -> Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    isCurrent -> Text(
                        text = (index + 1).toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    else -> Text(
                        text = (index + 1).toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Timer info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Section name (if first timer in section or section has repeats)
                if (flattenedTimer.sectionRepeat > 1) {
                    Text(
                        text = "${section.name} (×${flattenedTimer.sectionRepeat}/${flattenedTimer.totalSectionRepeats})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = timer.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )

                // Progress bar
                if (progress > 0f) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Duration
            Text(
                text = if (isCurrent && (timerState is TimerState.Running || timerState is TimerState.Paused)) {
                    val remaining = when (timerState) {
                        is TimerState.Running -> timerState.remainingSeconds
                        is TimerState.Paused -> timerState.remainingSeconds
                        else -> timer.durationSeconds
                    }
                    TimeFormatter.formatTime(remaining)
                } else {
                    TimeFormatter.formatTime(timer.durationSeconds)
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WorkoutCompletedContent(
    workoutName: String,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "✅",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.workout_complete),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.workout_complete_message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onDone) {
            Text(stringResource(R.string.done))
        }
    }
}

@Composable
private fun StopConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.stop)) },
        text = {
            Text("Are you sure you want to stop the workout? Your progress will be lost.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.stop))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
