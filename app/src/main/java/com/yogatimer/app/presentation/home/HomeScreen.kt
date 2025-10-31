package com.yogatimer.app.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yogatimer.app.R
import com.yogatimer.app.domain.model.Workout
import com.yogatimer.app.presentation.components.WorkoutCard

/**
 * Home Screen displaying list of workouts.
 *
 * Design Specifications:
 * - Top App Bar: 64dp height, surface color, no elevation
 * - Cards: 16dp horizontal margin, 12dp vertical spacing
 * - FAB: 56dp, 16dp from edges, primaryContainer
 * - Empty state: Centered icon and text
 *
 * Features:
 * - Display all workouts in a list
 * - Create new workout (FAB)
 * - Edit/Delete workout (context menu)
 * - Navigate to settings
 * - Navigate to active timer when workout is clicked
 *
 * @param onWorkoutClick Callback when workout is clicked to start
 * @param onCreateWorkout Callback to navigate to create workout screen
 * @param onEditWorkout Callback to navigate to edit workout screen
 * @param onNavigateToSettings Callback to navigate to settings screen
 * @param viewModel HomeViewModel instance
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onWorkoutClick: (Workout) -> Unit,
    onCreateWorkout: () -> Unit,
    onEditWorkout: (Workout) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle navigation events
    LaunchedEffect(uiState.selectedWorkout) {
        uiState.selectedWorkout?.let { workout ->
            onWorkoutClick(workout)
            viewModel.onNavigationHandled()
        }
    }

    LaunchedEffect(uiState.shouldNavigateToCreate) {
        if (uiState.shouldNavigateToCreate) {
            onCreateWorkout()
            viewModel.onNavigationHandled()
        }
    }

    LaunchedEffect(uiState.workoutToEdit) {
        uiState.workoutToEdit?.let { workout ->
            onEditWorkout(workout)
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
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onCreateWorkout() },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create workout"
                )
            }
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
                    // Loading state
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.workouts.isEmpty() -> {
                    // Empty state
                    EmptyState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    // Workout list
                    WorkoutList(
                        workouts = uiState.workouts,
                        onWorkoutClick = { viewModel.onWorkoutClick(it) },
                        onWorkoutEdit = { viewModel.onEditWorkout(it) },
                        onWorkoutDelete = { viewModel.onDeleteWorkoutRequest(it) }
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    uiState.workoutToDelete?.let { workout ->
        DeleteConfirmationDialog(
            workoutName = workout.name,
            onConfirm = { viewModel.confirmDeleteWorkout() },
            onDismiss = { viewModel.cancelDeleteWorkout() }
        )
    }
}

@Composable
private fun WorkoutList(
    workouts: List<Workout>,
    onWorkoutClick: (Workout) -> Unit,
    onWorkoutEdit: (Workout) -> Unit,
    onWorkoutDelete: (Workout) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = workouts,
            key = { it.id }
        ) { workout ->
            WorkoutCard(
                workout = workout,
                progress = null, // TODO: Get actual progress from active sessions
                onClick = { onWorkoutClick(workout) },
                onEdit = { onWorkoutEdit(workout) },
                onDelete = { onWorkoutDelete(workout) }
            )
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Timer emoji
        Text(
            text = "⏱️",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.size(64.dp)
        )

        Text(
            text = stringResource(R.string.home_empty_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = stringResource(R.string.home_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    workoutName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Workout") },
        text = {
            Text("Are you sure you want to delete \"$workoutName\"? This action cannot be undone.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
