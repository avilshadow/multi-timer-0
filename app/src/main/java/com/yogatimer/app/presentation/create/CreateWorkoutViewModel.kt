package com.yogatimer.app.presentation.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogatimer.app.domain.model.Section
import com.yogatimer.app.domain.model.Timer
import com.yogatimer.app.domain.model.Workout
import com.yogatimer.app.domain.usecase.workout.CreateWorkoutUseCase
import com.yogatimer.app.domain.usecase.workout.GetWorkoutByIdUseCase
import com.yogatimer.app.domain.usecase.workout.UpdateWorkoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Create/Edit Workout Screen.
 *
 * Handles building and editing workouts with nested sections and timers.
 */
@HiltViewModel
class CreateWorkoutViewModel @Inject constructor(
    private val createWorkoutUseCase: CreateWorkoutUseCase,
    private val updateWorkoutUseCase: UpdateWorkoutUseCase,
    private val getWorkoutByIdUseCase: GetWorkoutByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workoutId: Long? = savedStateHandle.get<Long>("workoutId")
    private val isEditMode = workoutId != null && workoutId > 0

    private val _uiState = MutableStateFlow(CreateWorkoutUiState(isEditMode = isEditMode))
    val uiState: StateFlow<CreateWorkoutUiState> = _uiState.asStateFlow()

    init {
        if (isEditMode && workoutId != null) {
            loadWorkout(workoutId)
        }
    }

    /**
     * Load existing workout for editing.
     */
    private fun loadWorkout(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val workout = getWorkoutByIdUseCase(id).first()
                _uiState.update {
                    it.copy(
                        name = workout.name,
                        description = workout.description,
                        sections = workout.sections.toMutableList(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load workout"
                    )
                }
            }
        }
    }

    /**
     * Update workout name.
     */
    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }

    /**
     * Update workout description.
     */
    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    /**
     * Show dialog to add new section.
     */
    fun onAddSectionClick(parentSection: Section? = null) {
        _uiState.update {
            it.copy(
                showAddSectionDialog = true,
                parentSectionForNewItem = parentSection
            )
        }
    }

    /**
     * Show dialog to add new timer to section.
     */
    fun onAddTimerClick(parentSection: Section) {
        _uiState.update {
            it.copy(
                showAddTimerDialog = true,
                parentSectionForNewItem = parentSection
            )
        }
    }

    /**
     * Add new section with given properties.
     */
    fun addSection(name: String, description: String, repeatCount: Int) {
        val newSection = Section(
            id = 0, // Will be assigned by database
            name = name,
            description = description,
            repeatCount = repeatCount,
            timers = emptyList(),
            childSections = emptyList(),
            level = _uiState.value.parentSectionForNewItem?.level?.plus(1) ?: 0
        )

        _uiState.update { state ->
            val updatedSections = state.sections.toMutableList()
            val parentSection = state.parentSectionForNewItem

            if (parentSection == null) {
                // Add to root level
                updatedSections.add(newSection)
            } else {
                // Add to parent section
                updateSectionInList(updatedSections, parentSection.id) { section ->
                    section.copy(childSections = section.childSections + newSection)
                }
            }

            state.copy(
                sections = updatedSections,
                showAddSectionDialog = false,
                parentSectionForNewItem = null
            )
        }
    }

    /**
     * Add new timer with given properties.
     */
    fun addTimer(name: String, description: String, durationSeconds: Int) {
        val newTimer = Timer(
            id = 0, // Will be assigned by database
            name = name,
            description = description,
            durationSeconds = durationSeconds
        )

        _uiState.update { state ->
            val updatedSections = state.sections.toMutableList()
            val parentSection = state.parentSectionForNewItem

            if (parentSection != null) {
                updateSectionInList(updatedSections, parentSection.id) { section ->
                    section.copy(timers = section.timers + newTimer)
                }
            }

            state.copy(
                sections = updatedSections,
                showAddTimerDialog = false,
                parentSectionForNewItem = null
            )
        }
    }

    /**
     * Delete section from workout.
     */
    fun deleteSection(section: Section) {
        _uiState.update { state ->
            val updatedSections = removeSectionFromList(state.sections.toMutableList(), section.id)
            state.copy(sections = updatedSections)
        }
    }

    /**
     * Delete timer from section.
     */
    fun deleteTimer(section: Section, timer: Timer) {
        _uiState.update { state ->
            val updatedSections = state.sections.toMutableList()
            updateSectionInList(updatedSections, section.id) { s ->
                s.copy(timers = s.timers.filter { it.id != timer.id })
            }
            state.copy(sections = updatedSections)
        }
    }

    /**
     * Cancel dialogs.
     */
    fun cancelDialog() {
        _uiState.update {
            it.copy(
                showAddSectionDialog = false,
                showAddTimerDialog = false,
                parentSectionForNewItem = null
            )
        }
    }

    /**
     * Save workout.
     */
    fun saveWorkout() {
        val state = _uiState.value

        // Validate
        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Name is required") }
            return
        }

        if (state.sections.isEmpty()) {
            _uiState.update { it.copy(error = "Add at least one section") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            try {
                val workout = Workout(
                    id = workoutId ?: 0,
                    name = state.name,
                    description = state.description,
                    sections = state.sections,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                if (isEditMode && workoutId != null) {
                    updateWorkoutUseCase(workout)
                } else {
                    createWorkoutUseCase(workout)
                }

                _uiState.update { it.copy(isSaving = false, shouldNavigateBack = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "Failed to save workout"
                    )
                }
            }
        }
    }

    /**
     * Clear navigation flag after navigation is handled.
     */
    fun onNavigationHandled() {
        _uiState.update { it.copy(shouldNavigateBack = false) }
    }

    /**
     * Dismiss error message.
     */
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    // Helper functions

    private fun updateSectionInList(
        sections: MutableList<Section>,
        sectionId: Long,
        update: (Section) -> Section
    ) {
        for (i in sections.indices) {
            val section = sections[i]
            if (section.id == sectionId) {
                sections[i] = update(section)
                return
            }

            // Check child sections recursively
            val updatedChildren = section.childSections.toMutableList()
            updateSectionInList(updatedChildren, sectionId, update)
            if (updatedChildren != section.childSections) {
                sections[i] = section.copy(childSections = updatedChildren)
                return
            }
        }
    }

    private fun removeSectionFromList(
        sections: MutableList<Section>,
        sectionId: Long
    ): List<Section> {
        val result = mutableListOf<Section>()

        for (section in sections) {
            if (section.id == sectionId) {
                continue // Skip this section
            }

            // Process child sections recursively
            val updatedChildren = removeSectionFromList(
                section.childSections.toMutableList(),
                sectionId
            )

            result.add(section.copy(childSections = updatedChildren))
        }

        return result
    }
}

/**
 * UI State for Create/Edit Workout Screen.
 */
data class CreateWorkoutUiState(
    val isEditMode: Boolean = false,
    val name: String = "",
    val description: String = "",
    val sections: List<Section> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val nameError: String? = null,
    val showAddSectionDialog: Boolean = false,
    val showAddTimerDialog: Boolean = false,
    val parentSectionForNewItem: Section? = null,
    val shouldNavigateBack: Boolean = false
)
