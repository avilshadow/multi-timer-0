package com.yogatimer.app.presentation.create

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yogatimer.app.R
import com.yogatimer.app.domain.model.Section
import com.yogatimer.app.domain.model.Timer
import com.yogatimer.app.util.TimeFormatter

/**
 * Create/Edit Workout Screen.
 *
 * Design Specifications:
 * - Top App Bar with back button and save (checkmark)
 * - Text fields for name and description
 * - Section cards with nested support
 * - Add section/timer buttons
 * - Dialogs for adding items
 *
 * @param onBack Callback to navigate back
 * @param viewModel CreateWorkoutViewModel instance
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    onBack: () -> Unit,
    viewModel: CreateWorkoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
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
                        text = if (uiState.isEditMode) {
                            "Edit Workout"
                        } else {
                            stringResource(R.string.nav_create_workout)
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.saveWorkout() },
                        enabled = !uiState.isSaving
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator()
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(R.string.save)
                            )
                        }
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
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                WorkoutForm(
                    name = uiState.name,
                    description = uiState.description,
                    nameError = uiState.nameError,
                    sections = uiState.sections,
                    onNameChange = viewModel::onNameChange,
                    onDescriptionChange = viewModel::onDescriptionChange,
                    onAddSectionClick = viewModel::onAddSectionClick,
                    onAddTimerClick = viewModel::onAddTimerClick,
                    onDeleteSection = viewModel::deleteSection,
                    onDeleteTimer = viewModel::deleteTimer
                )
            }
        }
    }

    // Add Section Dialog
    if (uiState.showAddSectionDialog) {
        AddSectionDialog(
            onDismiss = { viewModel.cancelDialog() },
            onConfirm = { name, description, repeatCount ->
                viewModel.addSection(name, description, repeatCount)
            }
        )
    }

    // Add Timer Dialog
    if (uiState.showAddTimerDialog) {
        AddTimerDialog(
            onDismiss = { viewModel.cancelDialog() },
            onConfirm = { name, description, durationSeconds ->
                viewModel.addTimer(name, description, durationSeconds)
            }
        )
    }
}

@Composable
private fun WorkoutForm(
    name: String,
    description: String,
    nameError: String?,
    sections: List<Section>,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAddSectionClick: (Section?) -> Unit,
    onAddTimerClick: (Section) -> Unit,
    onDeleteSection: (Section) -> Unit,
    onDeleteTimer: (Section, Timer) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Workout Name
        item {
            Column {
                Text(
                    text = stringResource(R.string.workout_name),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it) } },
                    singleLine = true
                )
            }
        }

        // Description
        item {
            Column {
                Text(
                    text = stringResource(R.string.description_optional),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
            }
        }

        // Sections Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.sections),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onAddSectionClick(null) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Section")
                }
            }
        }

        // Section Cards
        items(
            items = sections,
            key = { it.hashCode() }
        ) { section ->
            SectionCard(
                section = section,
                level = 0,
                onAddSectionClick = onAddSectionClick,
                onAddTimerClick = onAddTimerClick,
                onDeleteSection = onDeleteSection,
                onDeleteTimer = onDeleteTimer
            )
        }

        // Add Section Button (if no sections)
        if (sections.isEmpty()) {
            item {
                OutlinedButton(
                    onClick = { onAddSectionClick(null) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.add_section))
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    section: Section,
    level: Int,
    onAddSectionClick: (Section?) -> Unit,
    onAddTimerClick: (Section) -> Unit,
    onDeleteSection: (Section) -> Unit,
    onDeleteTimer: (Section, Timer) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (level * 16).dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Section header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.DragHandle,
                        contentDescription = stringResource(R.string.cd_reorder),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = section.name + if (section.repeatCount > 1) " (×${section.repeatCount})" else "",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.cd_more_options)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.delete)) },
                            onClick = {
                                showMenu = false
                                onDeleteSection(section)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            }
                        )
                    }
                }
            }

            // Timers
            section.timers.forEach { timer ->
                TimerItem(
                    timer = timer,
                    onDelete = { onDeleteTimer(section, timer) }
                )
            }

            // Child sections
            section.childSections.forEach { childSection ->
                Spacer(modifier = Modifier.height(8.dp))
                SectionCard(
                    section = childSection,
                    level = level + 1,
                    onAddSectionClick = onAddSectionClick,
                    onAddTimerClick = onAddTimerClick,
                    onDeleteSection = onDeleteSection,
                    onDeleteTimer = onDeleteTimer
                )
            }

            // Add buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = { onAddTimerClick(section) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.add_timer))
                }

                TextButton(
                    onClick = { onAddSectionClick(section) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.add_section))
                }
            }
        }
    }
}

@Composable
private fun TimerItem(
    timer: Timer,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "• ${timer.name} (${TimeFormatter.formatTime(timer.durationSeconds)})",
                style = MaterialTheme.typography.bodyMedium
            )
            if (timer.description.isNotBlank()) {
                Text(
                    text = timer.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun AddSectionDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String, repeatCount: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var repeatCount by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_section)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.section_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description_optional)) },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = repeatCount,
                    onValueChange = { if (it.all { c -> c.isDigit() }) repeatCount = it },
                    label = { Text(stringResource(R.string.repeat_count)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, description, repeatCount.toIntOrNull() ?: 1)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun AddTimerDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String, durationSeconds: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("0") }
    var seconds by remember { mutableStateOf("30") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_timer)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.timer_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description_optional)) },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.duration),
                    style = MaterialTheme.typography.labelMedium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = minutes,
                        onValueChange = { if (it.all { c -> c.isDigit() }) minutes = it },
                        label = { Text("Minutes") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    Text(":")

                    OutlinedTextField(
                        value = seconds,
                        onValueChange = { if (it.all { c -> c.isDigit() }) seconds = it },
                        label = { Text("Seconds") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val totalSeconds = (minutes.toIntOrNull() ?: 0) * 60 + (seconds.toIntOrNull() ?: 0)
                        if (totalSeconds > 0) {
                            onConfirm(name, description, totalSeconds)
                        }
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
