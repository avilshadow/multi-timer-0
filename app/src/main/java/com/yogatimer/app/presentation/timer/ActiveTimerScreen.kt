package com.yogatimer.app.presentation.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yogatimer.app.R
import com.yogatimer.app.domain.model.TimerState
import com.yogatimer.app.presentation.components.CircularTimerProgress
import com.yogatimer.app.presentation.components.OverallWorkoutProgress
import com.yogatimer.app.presentation.components.SectionRepeatProgress
import com.yogatimer.app.presentation.components.TimerControls

/**
 * Active Timer Screen displaying running workout.
 *
 * Design Specifications (from DESIGN_SYSTEM.md):
 * - Section name: headlineMedium, Bold
 * - Section repeat progress: 12dp height, dual-layer
 * - Circular timer: 280dp diameter, animated
 * - Timer name: titleLarge
 * - Controls: 64dp touch targets, 24dp spacing
 * - Overall progress: 8dp height, secondary color
 * - Vertical spacing: 24dp, 32dp as specified
 *
 * Layout:
 * - All content centered horizontally
 * - Max width: 360dp on tablets
 * - Safe area padding: 24dp top/bottom
 *
 * @param onBack Callback to navigate back
 * @param viewModel ActiveTimerViewModel instance
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
                    IconButton(onClick = { viewModel.onStopRequest() }) {
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

                timerState is TimerState.Idle -> {
                    // This shouldn't normally happen
                    Text(
                        text = "Timer not started",
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
                    // Running or Paused state
                    ActiveTimerContent(
                        timerState = timerState,
                        onPauseResume = viewModel::onPauseResume,
                        onSkip = viewModel::onSkip,
                        onStop = { viewModel.onStopRequest() }
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
private fun ActiveTimerContent(
    timerState: TimerState,
    onPauseResume: () -> Unit,
    onSkip: () -> Unit,
    onStop: () -> Unit
) {
    // Extract data from state
    val section = when (timerState) {
        is TimerState.Running -> timerState.currentSection
        is TimerState.Paused -> timerState.currentSection
        else -> return
    }

    val timer = when (timerState) {
        is TimerState.Running -> timerState.currentTimer
        is TimerState.Paused -> timerState.currentTimer
        else -> return
    }

    val remainingSeconds = when (timerState) {
        is TimerState.Running -> timerState.remainingSeconds
        is TimerState.Paused -> timerState.remainingSeconds
        else -> return
    }

    val sectionProgress = when (timerState) {
        is TimerState.Running -> timerState.sectionProgress
        is TimerState.Paused -> timerState.sectionProgress
        else -> return
    }

    val overallProgress = when (timerState) {
        is TimerState.Running -> timerState.overallProgress
        is TimerState.Paused -> timerState.overallProgress
        else -> return
    }

    val isRunning = timerState is TimerState.Running

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Section name
        Text(
            text = section.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Section repeat progress (if section has repeats)
        if (sectionProgress.totalRepeats > 1) {
            SectionRepeatProgress(
                currentRepeat = sectionProgress.currentRepeat,
                totalRepeats = sectionProgress.totalRepeats,
                currentTimerIndex = sectionProgress.currentTimerIndex,
                totalTimers = sectionProgress.totalTimers
            )
            Spacer(modifier = Modifier.height(24.dp))
        } else {
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Circular timer progress
        CircularTimerProgress(
            remainingSeconds = remainingSeconds,
            totalSeconds = timer.durationSeconds
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Timer name
        Text(
            text = timer.name,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        // Timer description (if present)
        if (timer.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = timer.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Control buttons
        TimerControls(
            isRunning = isRunning,
            onPauseResume = onPauseResume,
            onSkip = onSkip,
            onStop = onStop
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Overall progress
        OverallWorkoutProgress(
            currentExercise = overallProgress.currentTimerGlobal,
            totalExercises = overallProgress.totalTimersGlobal,
            elapsedSeconds = overallProgress.elapsedSeconds,
            totalSeconds = overallProgress.totalSeconds
        )

        Spacer(modifier = Modifier.height(24.dp))
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
        // Checkmark emoji
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

        androidx.compose.material3.Button(
            onClick = onDone
        ) {
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
