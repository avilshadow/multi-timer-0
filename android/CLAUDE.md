# Yoga Timer - Android App Development Specification
## Complete PRD & Technical Documentation for Claude Code

**Version:** 1.0  
**Platform:** Android (Native Kotlin + Jetpack Compose)  
**Target SDK:** Android 15 (API 35)  
**Minimum SDK:** Android 8.0 (API 26)

---

## Table of Contents
1. [Overview](#overview)
2. [Database Schema](#database-schema)
3. [Architecture & Project Structure](#architecture--project-structure)
4. [Feature Specifications](#feature-specifications)
5. [UI Specifications & Wireframes](#ui-specifications--wireframes)
6. [User Flows](#user-flows)
7. [Technical Implementation Details](#technical-implementation-details)
8. [Acceptance Criteria](#acceptance-criteria)

---

## Overview

### Product Description
A discreet interval timer application for yoga, meditation, and workout routines. Features automatic progression through timed exercises with TTS announcements, nested repeat structures, visual/audio feedback, and lock screen integration.

### Core Features
- Sequential timer flow with automatic progression
- Text-to-Speech (TTS) for section names and descriptions
- Nested repeat structures (sections within repeats)
- Dual progress indicators (total + current repeat)
- Lock screen display and controls
- Configurable audio feedback (bells, system sounds)
- Background execution with notifications

---

## Database Schema

### Entity Relationship Diagram
```
Workout (1) ──────── (N) Section
                           │
                           │ (1)
                           │
                           (N) Timer
```

### 1. Workout Entity
```kotlin
@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,                          // e.g., "Morning Yoga Flow"
    val description: String = "",              // Optional workout description
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPreloaded: Boolean = false,          // True for example workouts
    val sortOrder: Int = 0                     // For custom ordering
)
```

### 2. Section Entity
```kotlin
@Entity(
    tableName = "sections",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutId"), Index("parentSectionId")]
)
data class SectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val workoutId: Long,                       // FK to Workout
    val parentSectionId: Long? = null,         // FK to parent Section (for nesting)
    
    val name: String,                          // e.g., "Sun Salutation A"
    val description: String = "",              // TTS announcement text
    val repeatCount: Int = 1,                  // How many times to repeat this section
    val sortOrder: Int = 0,                    // Order within parent
    val level: Int = 0                         // Nesting level (0 = root, 1 = nested, etc.)
)
```

### 3. Timer Entity
```kotlin
@Entity(
    tableName = "timers",
    foreignKeys = [
        ForeignKey(
            entity = SectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sectionId")]
)
data class TimerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val sectionId: Long,                       // FK to Section
    
    val name: String,                          // e.g., "Downward Dog"
    val description: String = "",              // TTS announcement text
    val durationSeconds: Int,                  // Duration in seconds
    val sortOrder: Int = 0                     // Order within section
)
```

### 4. Settings Entity
```kotlin
@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    val id: Int = 1,                           // Always 1 (singleton)
    
    // Display Settings
    val keepScreenOn: Boolean = true,
    val theme: String = "SYSTEM",              // LIGHT, DARK, SYSTEM
    
    // Audio Settings
    val enableTTS: Boolean = true,
    val ttsLanguage: String = "en-US",
    val enableSoundEffects: Boolean = true,
    val completionSoundUri: String = "system_default", // URI or "system_default"
    val soundVolume: Float = 0.7f,             // 0.0 to 1.0
    
    // Haptic Settings
    val enableVibration: Boolean = true,
    
    // Notification Settings
    val showLockScreenControls: Boolean = true
)
```

### 5. DAO Interfaces

```kotlin
@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts ORDER BY sortOrder ASC, createdAt DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>
    
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Long): WorkoutEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long
    
    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)
    
    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)
}

@Dao
interface SectionDao {
    @Query("SELECT * FROM sections WHERE workoutId = :workoutId ORDER BY sortOrder ASC")
    fun getSectionsForWorkout(workoutId: Long): Flow<List<SectionEntity>>
    
    @Query("SELECT * FROM sections WHERE parentSectionId = :parentId ORDER BY sortOrder ASC")
    suspend fun getChildSections(parentId: Long): List<SectionEntity>
    
    @Query("SELECT * FROM sections WHERE id = :id")
    suspend fun getSectionById(id: Long): SectionEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(section: SectionEntity): Long
    
    @Update
    suspend fun updateSection(section: SectionEntity)
    
    @Delete
    suspend fun deleteSection(section: SectionEntity)
    
    @Query("DELETE FROM sections WHERE workoutId = :workoutId")
    suspend fun deleteSectionsForWorkout(workoutId: Long)
}

@Dao
interface TimerDao {
    @Query("SELECT * FROM timers WHERE sectionId = :sectionId ORDER BY sortOrder ASC")
    suspend fun getTimersForSection(sectionId: Long): List<TimerEntity>
    
    @Query("SELECT * FROM timers WHERE id = :id")
    suspend fun getTimerById(id: Long): TimerEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimer(timer: TimerEntity): Long
    
    @Update
    suspend fun updateTimer(timer: TimerEntity)
    
    @Delete
    suspend fun deleteTimer(timer: TimerEntity)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettings(): Flow<SettingsEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: SettingsEntity)
}
```

### 6. Database Class

```kotlin
@Database(
    entities = [
        WorkoutEntity::class,
        SectionEntity::class,
        TimerEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class YogaTimerDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun sectionDao(): SectionDao
    abstract fun timerDao(): TimerDao
    abstract fun settingsDao(): SettingsDao
}
```

---

## Architecture & Project Structure

### Clean Architecture Layers

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Compose UI + ViewModels)              │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────┴───────────────────────┐
│          Domain Layer                   │
│  (Use Cases + Business Logic)           │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────┴───────────────────────┐
│           Data Layer                    │
│  (Repositories + Room + DataStore)      │
└─────────────────────────────────────────┘
```

### Project Directory Structure

```
app/src/main/java/com/yourcompany/yogatimer/
│
├── YogaTimerApplication.kt              # Application class
│
├── data/
│   ├── local/
│   │   ├── database/
│   │   │   ├── YogaTimerDatabase.kt
│   │   │   ├── dao/
│   │   │   │   ├── WorkoutDao.kt
│   │   │   │   ├── SectionDao.kt
│   │   │   │   ├── TimerDao.kt
│   │   │   │   └── SettingsDao.kt
│   │   │   └── entities/
│   │   │       ├── WorkoutEntity.kt
│   │   │       ├── SectionEntity.kt
│   │   │       ├── TimerEntity.kt
│   │   │       └── SettingsEntity.kt
│   │   └── datastore/
│   │       └── PreferencesManager.kt
│   │
│   └── repository/
│       ├── WorkoutRepositoryImpl.kt
│       ├── TimerRepositoryImpl.kt
│       └── SettingsRepositoryImpl.kt
│
├── domain/
│   ├── model/
│   │   ├── Workout.kt                   # Domain model
│   │   ├── Section.kt
│   │   ├── Timer.kt
│   │   ├── Settings.kt
│   │   └── WorkoutProgress.kt           # Runtime progress tracking
│   │
│   ├── repository/                      # Repository interfaces
│   │   ├── WorkoutRepository.kt
│   │   ├── TimerRepository.kt
│   │   └── SettingsRepository.kt
│   │
│   └── usecase/
│       ├── workout/
│       │   ├── GetAllWorkoutsUseCase.kt
│       │   ├── GetWorkoutByIdUseCase.kt
│       │   ├── CreateWorkoutUseCase.kt
│       │   ├── UpdateWorkoutUseCase.kt
│       │   └── DeleteWorkoutUseCase.kt
│       │
│       └── timer/
│           ├── StartWorkoutUseCase.kt
│           ├── PauseWorkoutUseCase.kt
│           ├── ResumeWorkoutUseCase.kt
│           ├── StopWorkoutUseCase.kt
│           └── SkipTimerUseCase.kt
│
├── presentation/
│   ├── navigation/
│   │   └── NavGraph.kt
│   │
│   ├── screens/
│   │   ├── home/
│   │   │   ├── HomeScreen.kt
│   │   │   └── HomeViewModel.kt
│   │   │
│   │   ├── workout/
│   │   │   ├── create/
│   │   │   │   ├── CreateWorkoutScreen.kt
│   │   │   │   └── CreateWorkoutViewModel.kt
│   │   │   │
│   │   │   └── active/
│   │   │       ├── ActiveTimerScreen.kt
│   │   │       └── ActiveTimerViewModel.kt
│   │   │
│   │   └── settings/
│   │       ├── SettingsScreen.kt
│   │       └── SettingsViewModel.kt
│   │
│   ├── components/                      # Reusable UI components
│   │   ├── TimerCard.kt
│   │   ├── SectionCard.kt
│   │   ├── CircularProgressIndicator.kt
│   │   ├── ProgressBar.kt
│   │   └── DurationPicker.kt
│   │
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
│
├── service/
│   ├── timer/
│   │   ├── TimerService.kt              # Foreground service
│   │   └── TimerManager.kt              # Core timer logic
│   │
│   ├── tts/
│   │   └── TTSManager.kt                # Text-to-Speech manager
│   │
│   ├── audio/
│   │   └── AudioManager.kt              # Sound effects
│   │
│   └── notification/
│       └── NotificationManager.kt       # Lock screen notifications
│
├── worker/
│   └── TimerWorker.kt                   # Background work
│
├── di/                                  # Dependency Injection
│   ├── DatabaseModule.kt
│   ├── RepositoryModule.kt
│   ├── UseCaseModule.kt
│   └── ServiceModule.kt
│
└── util/
    ├── Constants.kt
    ├── Extensions.kt
    └── TimeFormatter.kt
```

---

## Feature Specifications

### Feature 1: Workout Management

#### User Stories
- As a user, I want to create custom workout sequences
- As a user, I want to organize timers into sections with repeats
- As a user, I want to nest sections for complex workout structures
- As a user, I want to save and load workouts

#### Technical Requirements

**1.1 Workout Creation**
```kotlin
// Domain Model
data class Workout(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val sections: List<Section> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPreloaded: Boolean = false
)

data class Section(
    val id: Long = 0,
    val workoutId: Long,
    val parentSectionId: Long? = null,
    val name: String,
    val description: String = "",           // For TTS
    val repeatCount: Int = 1,
    val timers: List<Timer> = emptyList(),
    val childSections: List<Section> = emptyList(),
    val level: Int = 0
)

data class Timer(
    val id: Long = 0,
    val sectionId: Long,
    val name: String,
    val description: String = "",           // For TTS
    val durationSeconds: Int
)
```

**1.2 Nested Repeat Structure**

Example workout structure:
```
Workout: "Advanced Yoga Flow"
├── Section: "Warm Up" (repeat: 1)
│   ├── Timer: "Child's Pose" (60s)
│   └── Timer: "Cat-Cow" (30s)
│
└── Section: "Main Flow" (repeat: 3)      ← Wrapper repeat
    ├── Section: "Right Side" (repeat: 2) ← Nested section with repeat
    │   ├── Timer: "Warrior I" (45s)
    │   └── Timer: "Warrior II" (45s)
    │
    └── Section: "Left Side" (repeat: 2)  ← Nested section with repeat
        ├── Timer: "Warrior I" (45s)
        └── Timer: "Warrior II" (45s)
```

**Progress Tracking Logic:**
- Each section with `repeatCount > 1` shows:
  - Total progress (darker fill): e.g., "Repeat 2/3"
  - Current repeat progress (lighter fill): current timer within repeat
- Progress calculation:
  ```kotlin
  data class SectionProgress(
      val sectionId: Long,
      val currentRepeat: Int,         // 1-based
      val totalRepeats: Int,
      val currentTimerIndex: Int,     // 0-based
      val totalTimers: Int
  )
  ```

#### Acceptance Criteria
- ✅ User can create workout with name and description
- ✅ User can add sections to workout
- ✅ User can nest sections within sections
- ✅ User can set repeat count for any section (1-99)
- ✅ User can add timers to sections
- ✅ User can set timer duration (1 second to 99 minutes)
- ✅ User can reorder sections and timers via drag-and-drop
- ✅ User can save workout to local database
- ✅ User can edit existing workouts
- ✅ User can delete workouts (with confirmation)
- ✅ Validation: Workout name required, must have at least 1 timer

---

### Feature 2: Timer Execution Engine

#### User Stories
- As a user, I want timers to countdown automatically
- As a user, I want automatic progression to next timer
- As a user, I want to hear TTS announcements for sections
- As a user, I want to hear completion sounds
- As a user, I want to see dual progress indicators

#### Technical Requirements

**2.1 Timer State Machine**
```kotlin
sealed class TimerState {
    object Idle : TimerState()
    data class Running(
        val currentSection: Section,
        val currentTimer: Timer,
        val remainingSeconds: Int,
        val sectionProgress: SectionProgress,
        val overallProgress: WorkoutProgress
    ) : TimerState()
    data class Paused(
        val currentSection: Section,
        val currentTimer: Timer,
        val remainingSeconds: Int,
        val sectionProgress: SectionProgress,
        val overallProgress: WorkoutProgress
    ) : TimerState()
    object Completed : TimerState()
}

data class WorkoutProgress(
    val currentSectionIndex: Int,         // Flattened index
    val totalSections: Int,               // Total after expansion
    val currentTimerGlobal: Int,          // Global timer number
    val totalTimersGlobal: Int,           // Total timers in workout
    val elapsedSeconds: Int,
    val totalSeconds: Int
)
```

**2.2 Timer Manager**
```kotlin
class TimerManager @Inject constructor(
    private val ttsManager: TTSManager,
    private val audioManager: AudioManager,
    private val notificationManager: NotificationManager
) {
    private val _timerState = MutableStateFlow<TimerState>(TimerState.Idle)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()
    
    private var countdownJob: Job? = null
    private var currentWorkoutStructure: ExpandedWorkout? = null
    
    fun startWorkout(workout: Workout) {
        // 1. Expand workout structure (flatten repeats)
        currentWorkoutStructure = expandWorkout(workout)
        
        // 2. Start first timer
        startNextTimer()
    }
    
    fun pause() { /* Pause countdown */ }
    fun resume() { /* Resume countdown */ }
    fun stop() { /* Stop and reset */ }
    fun skipToNext() { /* Skip current timer */ }
    
    private fun startNextTimer() {
        // 1. Get next timer from expanded structure
        // 2. Check if section just started → TTS announcement
        // 3. Start countdown
        // 4. Update progress
    }
    
    private fun onTimerComplete() {
        // 1. Play completion sound
        // 2. Trigger haptic feedback
        // 3. Check if section complete → TTS announcement + bell sound
        // 4. Move to next timer or complete workout
    }
    
    private fun expandWorkout(workout: Workout): ExpandedWorkout {
        // Flatten nested structure accounting for repeats
        // Return linear list of timers with section metadata
    }
}
```

**2.3 Text-to-Speech Integration**
```kotlin
class TTSManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var tts: TextToSpeech? = null
    private var isReady = false
    
    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                isReady = true
            }
        }
    }
    
    fun announceSection(section: Section) {
        if (!isReady) return
        
        val announcement = buildString {
            append(section.name)
            if (section.description.isNotEmpty()) {
                append(". ")
                append(section.description)
            }
            if (section.repeatCount > 1) {
                append(". Repeat ${section.repeatCount} times.")
            }
        }
        
        tts?.speak(announcement, TextToSpeech.QUEUE_ADD, null, "section_${section.id}")
    }
    
    fun announceTimer(timer: Timer) {
        if (!isReady || timer.description.isEmpty()) return
        
        tts?.speak(timer.description, TextToSpeech.QUEUE_ADD, null, "timer_${timer.id}")
    }
    
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
```

**2.4 Audio Feedback**
```kotlin
class AudioManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) {
    private var mediaPlayer: MediaPlayer? = null
    
    suspend fun playCompletionSound() {
        val settings = settingsRepository.getSettings().first()
        if (!settings.enableSoundEffects) return
        
        mediaPlayer?.release()
        
        val soundUri = when (settings.completionSoundUri) {
            "system_default" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            else -> Uri.parse(settings.completionSoundUri)
        }
        
        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, soundUri)
            setVolume(settings.soundVolume, settings.soundVolume)
            prepare()
            start()
        }
    }
    
    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
```

#### Acceptance Criteria
- ✅ Timer counts down every second accurately
- ✅ TTS announces section name + description when section starts
- ✅ TTS announces timer description (if provided) when timer starts
- ✅ Completion sound plays at end of each section repeat
- ✅ Automatic progression to next timer without user interaction
- ✅ Timers continue in background (with notification)
- ✅ Pause/resume works correctly
- ✅ Skip moves to next timer immediately
- ✅ Stop resets workout to beginning
- ✅ Progress indicators update in real-time

---

### Feature 3: Progress Visualization

#### User Stories
- As a user, I want to see how much time remains in current timer
- As a user, I want to see progress within current section repeat
- As a user, I want to see progress across all section repeats
- As a user, I want to see overall workout progress

#### Technical Requirements

**3.1 Progress UI Components**

```kotlin
@Composable
fun TimerDisplay(
    state: TimerState.Running,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Current Section Name
        Text(
            text = state.currentSection.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        // Section Repeat Progress (if applicable)
        if (state.currentSection.repeatCount > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            SectionRepeatProgress(progress = state.sectionProgress)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Main Timer Circle
        CircularTimerProgress(
            remainingSeconds = state.remainingSeconds,
            totalSeconds = state.currentTimer.durationSeconds,
            modifier = Modifier.size(280.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Current Timer Name
        Text(
            text = state.currentTimer.name,
            style = MaterialTheme.typography.titleLarge
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Overall Workout Progress
        OverallWorkoutProgress(progress = state.overallProgress)
    }
}

@Composable
fun SectionRepeatProgress(
    progress: SectionProgress,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Repeat ${progress.currentRepeat} of ${progress.totalRepeats}",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Dual progress bar
        Box(modifier = Modifier.fillMaxWidth(0.8f).height(12.dp)) {
            // Total progress (darker)
            LinearProgressIndicator(
                progress = {
                    (progress.currentRepeat - 1).toFloat() / progress.totalRepeats.toFloat()
                },
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(6.dp)),
                color = MaterialTheme.colorScheme.primaryContainer,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            // Current repeat progress (lighter, overlaid)
            LinearProgressIndicator(
                progress = {
                    val baseProgress = (progress.currentRepeat - 1).toFloat() / progress.totalRepeats.toFloat()
                    val currentProgress = progress.currentTimerIndex.toFloat() / progress.totalTimers.toFloat()
                    val incrementProgress = currentProgress / progress.totalRepeats.toFloat()
                    baseProgress + incrementProgress
                },
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(6.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent
            )
        }
    }
}

@Composable
fun CircularTimerProgress(
    remainingSeconds: Int,
    totalSeconds: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Background circle
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 12.dp
        )
        
        // Progress circle
        CircularProgressIndicator(
            progress = { 1f - (remainingSeconds.toFloat() / totalSeconds.toFloat()) },
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 12.dp
        )
        
        // Time display
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatTime(remainingSeconds),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Light
                )
            )
        }
    }
}

@Composable
fun OverallWorkoutProgress(
    progress: WorkoutProgress,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(0.8f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Exercise ${progress.currentTimerGlobal} of ${progress.totalTimersGlobal}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = formatTime(progress.elapsedSeconds) + " / " + formatTime(progress.totalSeconds),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LinearProgressIndicator(
            progress = { progress.currentTimerGlobal.toFloat() / progress.totalTimersGlobal.toFloat() },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

// Utility function
fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%d:%02d".format(mins, secs)
}
```

#### Acceptance Criteria
- ✅ Circular progress shows countdown for current timer
- ✅ Time display shows MM:SS format
- ✅ Section repeat progress shows "Repeat X of Y"
- ✅ Dual progress bar shows total progress (dark) + current repeat progress (light)
- ✅ Overall progress shows "Exercise X of Y" and time elapsed/total
- ✅ All progress indicators update smoothly every second
- ✅ UI remains responsive during countdown

---

### Feature 4: Lock Screen Integration

#### User Stories
- As a user, I want to see timer on lock screen
- As a user, I want to control timer from lock screen
- As a user, I want to see current exercise name on lock screen

#### Technical Requirements

**4.1 Foreground Service**
```kotlin
class TimerForegroundService : Service() {
    
    @Inject
    lateinit var timerManager: TimerManager
    
    @Inject
    lateinit var notificationHelper: TimerNotificationHelper
    
    private val binder = LocalBinder()
    private var notificationJob: Job? = null
    
    inner class LocalBinder : Binder() {
        fun getService(): TimerForegroundService = this@TimerForegroundService
    }
    
    override fun onBind(intent: Intent): IBinder = binder
    
    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, notificationHelper.createInitialNotification())
        observeTimerState()
    }
    
    private fun observeTimerState() {
        notificationJob = CoroutineScope(Dispatchers.Main).launch {
            timerManager.timerState.collect { state ->
                when (state) {
                    is TimerState.Running -> {
                        updateNotification(state)
                    }
                    is TimerState.Paused -> {
                        updateNotification(state)
                    }
                    is TimerState.Completed -> {
                        stopSelf()
                    }
                    else -> {}
                }
            }
        }
    }
    
    private fun updateNotification(state: TimerState) {
        val notification = notificationHelper.updateNotification(state)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    override fun onDestroy() {
        notificationJob?.cancel()
        super.onDestroy()
    }
    
    companion object {
        const val NOTIFICATION_ID = 1001
    }
}
```

**4.2 Notification Manager**
```kotlin
class TimerNotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Timer",
            NotificationManager.IMPORTANCE_LOW  // Low to not make sound
        ).apply {
            description = "Workout timer notifications"
            setShowBadge(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
    }
    
    fun createInitialNotification(): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("Yoga Timer")
            .setContentText("Starting workout...")
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .build()
    }
    
    fun updateNotification(state: TimerState): Notification {
        return when (state) {
            is TimerState.Running -> {
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_timer)
                    .setContentTitle(state.currentSection.name)
                    .setContentText("${state.currentTimer.name} - ${formatTime(state.remainingSeconds)}")
                    .setSubText("Exercise ${state.overallProgress.currentTimerGlobal} of ${state.overallProgress.totalTimersGlobal}")
                    .setOngoing(true)
                    .setCategory(NotificationCompat.CATEGORY_WORKOUT)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .addAction(createPauseAction())
                    .addAction(createSkipAction())
                    .addAction(createStopAction())
                    .setStyle(
                        androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                    )
                    .build()
            }
            is TimerState.Paused -> {
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_timer_paused)
                    .setContentTitle("${state.currentSection.name} (Paused)")
                    .setContentText("${state.currentTimer.name} - ${formatTime(state.remainingSeconds)}")
                    .setOngoing(true)
                    .addAction(createResumeAction())
                    .addAction(createStopAction())
                    .build()
            }
            else -> createInitialNotification()
        }
    }
    
    private fun createPauseAction(): NotificationCompat.Action {
        val intent = Intent(context, TimerActionReceiver::class.java).apply {
            action = ACTION_PAUSE
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Action(R.drawable.ic_pause, "Pause", pendingIntent)
    }
    
    private fun createResumeAction(): NotificationCompat.Action {
        val intent = Intent(context, TimerActionReceiver::class.java).apply {
            action = ACTION_RESUME
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Action(R.drawable.ic_play, "Resume", pendingIntent)
    }
    
    private fun createSkipAction(): NotificationCompat.Action {
        val intent = Intent(context, TimerActionReceiver::class.java).apply {
            action = ACTION_SKIP
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Action(R.drawable.ic_skip_next, "Skip", pendingIntent)
    }
    
    private fun createStopAction(): NotificationCompat.Action {
        val intent = Intent(context, TimerActionReceiver::class.java).apply {
            action = ACTION_STOP
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 3, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Action(R.drawable.ic_stop, "Stop", pendingIntent)
    }
    
    companion object {
        const val CHANNEL_ID = "timer_channel"
        const val ACTION_PAUSE = "com.yourcompany.yogatimer.PAUSE"
        const val ACTION_RESUME = "com.yourcompany.yogatimer.RESUME"
        const val ACTION_SKIP = "com.yourcompany.yogatimer.SKIP"
        const val ACTION_STOP = "com.yourcompany.yogatimer.STOP"
    }
}

class TimerActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Handle timer actions by communicating with service
        val action = intent.action
        val serviceIntent = Intent(context, TimerForegroundService::class.java).apply {
            putExtra("action", action)
        }
        context.startService(serviceIntent)
    }
}
```

#### Acceptance Criteria
- ✅ Timer shows on lock screen when running
- ✅ Lock screen shows: section name, timer name, remaining time
- ✅ Lock screen shows progress: "Exercise X of Y"
- ✅ Pause button works from lock screen
- ✅ Resume button works from lock screen
- ✅ Skip button works from lock screen
- ✅ Stop button works from lock screen
- ✅ Notification updates every second
- ✅ Notification persists when app is in background

---

## UI Specifications & Wireframes

### Screen 1: Home Screen

**Layout:**
```
┌─────────────────────────────────────┐
│ ☰  Yoga Timer            ⋮ Settings│
├─────────────────────────────────────┤
│                                     │
│  ┌─────────────────────────────┐   │
│  │ Morning Flow                │   │
│  │ 5 exercises · 15 min        │   │
│  │ ━━━━━━━━━━━░░░░░░ 60%      │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ Evening Stretch             │   │
│  │ 8 exercises · 20 min        │   │
│  │ Not started                 │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ Power Yoga                  │   │
│  │ 12 exercises · 30 min       │   │
│  │ Not started                 │   │
│  └─────────────────────────────┘   │
│                                     │
├─────────────────────────────────────┤
│                                [+]  │
└─────────────────────────────────────┘
```

**Specifications:**
- Top App Bar: Title on left, Settings icon on right
- Workout Cards:
  - Card elevation: 2dp
  - Padding: 16dp
  - Corner radius: 12dp
  - Tap to open workout detail
  - Long press for context menu (Edit/Delete)
- Progress bar if workout partially completed
- FAB (Floating Action Button) bottom right for "Create New Workout"
- Empty state if no workouts: "No workouts yet. Tap + to create one."

**Compose Code:**
```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCreate: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onWorkoutClick: (Long) -> Unit
) {
    val workouts by viewModel.workouts.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yoga Timer") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, "Create Workout")
            }
        }
    ) { padding ->
        if (workouts.isEmpty()) {
            EmptyState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(workouts, key = { it.id }) { workout ->
                    WorkoutCard(
                        workout = workout,
                        onClick = { onWorkoutClick(workout.id) },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }
}

@Composable
fun WorkoutCard(
    workout: Workout,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = workout.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            val totalTimers = workout.calculateTotalTimers()
            val totalDuration = workout.calculateTotalDuration()
            
            Text(
                text = "$totalTimers exercises · ${totalDuration / 60} min",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
```

---

### Screen 2: Create/Edit Workout Screen

**Layout:**
```
┌─────────────────────────────────────┐
│ ←  Create Workout            ✓ Save │
├─────────────────────────────────────┤
│ Workout Name                        │
│ ┌─────────────────────────────────┐ │
│ │ Morning Flow                    │ │
│ └─────────────────────────────────┘ │
│                                     │
│ Description (optional)              │
│ ┌─────────────────────────────────┐ │
│ │ Energizing flow to start the day│ │
│ └─────────────────────────────────┘ │
│                                     │
│ Sections                            │
│ ┌─────────────────────────────────┐ │
│ │ ≡  Warm Up              [↓] [✕]│ │
│ │    • Child's Pose (1:00)        │ │
│ │    • Cat-Cow (0:30)             │ │
│ │    + Add Timer                  │ │
│ └─────────────────────────────────┘ │
│                                     │
│ ┌─────────────────────────────────┐ │
│ │ ≡  Main Flow (×3)       [↓] [✕]│ │
│ │    └─ Right Side (×2)   [↓] [✕]│ │
│ │       • Warrior I (0:45)        │ │
│ │       + Add Timer               │ │
│ │    + Add Section                │ │
│ └─────────────────────────────────┘ │
│                                     │
│ [+ Add Section]                     │
│                                     │
└─────────────────────────────────────┘
```

**Specifications:**
- Text fields for workout name (required) and description
- Hierarchical section/timer list with:
  - Drag handles (≡) for reordering
  - Expand/collapse nested sections
  - Repeat count badge (×N) if count > 1
  - Edit button (pencil) to modify
  - Delete button (X) to remove
- Add Timer/Section buttons within appropriate context
- Save button validates and saves to database

**Compose Code:**
```kotlin
@Composable
fun CreateWorkoutScreen(
    workoutId: Long? = null,
    viewModel: CreateWorkoutViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (workoutId == null) "Create Workout" else "Edit Workout") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.saveWorkout() },
                        enabled = uiState.canSave
                    ) {
                        Icon(Icons.Default.Check, "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Workout Name
            OutlinedTextField(
                value = uiState.workoutName,
                onValueChange = { viewModel.updateWorkoutName(it) },
                label = { Text("Workout Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.showNameError,
                supportingText = if (uiState.showNameError) {
                    { Text("Name is required") }
                } else null
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Sections Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sections",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Section List
            uiState.sections.forEachIndexed { index, section ->
                SectionItem(
                    section = section,
                    level = 0,
                    onEdit = { viewModel.editSection(section.id) },
                    onDelete = { viewModel.deleteSection(section.id) },
                    onAddTimer = { viewModel.showAddTimerDialog(section.id) },
                    onAddChildSection = { viewModel.showAddSectionDialog(section.id) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Add Section Button
            OutlinedButton(
                onClick = { viewModel.showAddSectionDialog(null) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, "Add Section")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Section")
            }
        }
    }
    
    // Dialogs for adding/editing
    if (uiState.showSectionDialog) {
        AddSectionDialog(
            onDismiss = { viewModel.dismissDialogs() },
            onConfirm = { name, description, repeatCount ->
                viewModel.addSection(name, description, repeatCount)
            }
        )
    }
    
    if (uiState.showTimerDialog) {
        AddTimerDialog(
            onDismiss = { viewModel.dismissDialogs() },
            onConfirm = { name, description, durationSeconds ->
                viewModel.addTimer(name, description, durationSeconds)
            }
        )
    }
}

@Composable
fun SectionItem(
    section: Section,
    level: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddTimer: () -> Unit,
    onAddChildSection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = (level * 16).dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = "Reorder",
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = section.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                if (section.repeatCount > 1) {
                    Badge {
                        Text("×${section.repeatCount}")
                    }
                }
                
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, "Edit", modifier = Modifier.size(18.dp))
                }
                
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, "Delete", modifier = Modifier.size(18.dp))
                }
            }
            
            // Timers
            section.timers.forEach { timer ->
                TimerItem(timer = timer)
            }
            
            // Add Timer Button
            TextButton(onClick = onAddTimer) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Timer", style = MaterialTheme.typography.bodySmall)
            }
            
            // Child Sections
            section.childSections.forEach { childSection ->
                SectionItem(
                    section = childSection,
                    level = level + 1,
                    onEdit = onEdit,
                    onDelete = onDelete,
                    onAddTimer = onAddTimer,
                    onAddChildSection = onAddChildSection
                )
            }
            
            // Add Child Section Button
            if (level < 2) { // Limit nesting depth
                TextButton(onClick = onAddChildSection) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Nested Section", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun TimerItem(
    timer: Timer,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Timer,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = timer.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = formatTime(timer.durationSeconds),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

---

### Screen 3: Active Timer Screen

**Layout:**
```
┌─────────────────────────────────────┐
│ ←  Morning Flow                     │
├─────────────────────────────────────┤
│                                     │
│         Main Flow                   │
│       Repeat 2 of 3                 │
│   ━━━━━━━━━━━━░░░░░░░░░            │
│                                     │
│        ┌─────────────┐              │
│       ╱               ╲             │
│      │                 │            │
│      │                 │            │
│      │      1:45       │            │
│      │                 │            │
│      │                 │            │
│       ╲               ╱             │
│        └─────────────┘              │
│                                     │
│         Warrior I                   │
│                                     │
│       ⏸  ⏭  ⏹                      │
│                                     │
│  Exercise 5 of 12      8:23 / 15:00│
│  ━━━━━━━━░░░░░░░░░░░░░░░░░░        │
│                                     │
└─────────────────────────────────────┘
```

**Specifications:**
- Section name at top (headline medium)
- Repeat progress if section has repeats (title medium)
- Dual progress bar for section repeats
- Large circular countdown (280dp diameter)
- Timer name below circle (title large)
- Control buttons: Pause, Skip, Stop (48dp icons)
- Overall progress at bottom with linear progress bar
- Keep screen on (configurable in settings)

**Compose Code:**
```kotlin
@Composable
fun ActiveTimerScreen(
    workoutId: Long,
    viewModel: ActiveTimerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.timerState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    
    // Keep screen on
    val context = LocalContext.current
    DisposableEffect(settings.keepScreenOn) {
        val window = (context as? Activity)?.window
        if (settings.keepScreenOn) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(viewModel.workoutName) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.stopWorkout()
                        onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (val currentState = state) {
            is TimerState.Running -> {
                TimerDisplay(
                    state = currentState,
                    onPause = { viewModel.pauseWorkout() },
                    onSkip = { viewModel.skipTimer() },
                    onStop = {
                        viewModel.stopWorkout()
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
            is TimerState.Paused -> {
                TimerDisplay(
                    state = currentState,
                    onResume = { viewModel.resumeWorkout() },
                    onSkip = { viewModel.skipTimer() },
                    onStop = {
                        viewModel.stopWorkout()
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
            is TimerState.Completed -> {
                WorkoutCompletedScreen(
                    onDismiss = onNavigateBack
                )
            }
            else -> {
                // Loading or idle
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun TimerDisplay(
    state: TimerState,
    onPause: (() -> Unit)? = null,
    onResume: (() -> Unit)? = null,
    onSkip: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val runningState = when (state) {
        is TimerState.Running -> state
        is TimerState.Paused -> state
        else -> return
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Section: Section name and repeat progress
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = runningState.currentSection.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            if (runningState.currentSection.repeatCount > 1) {
                Spacer(modifier = Modifier.height(12.dp))
                SectionRepeatProgress(progress = runningState.sectionProgress)
            }
        }
        
        // Middle Section: Circular timer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularTimerProgress(
                remainingSeconds = runningState.remainingSeconds,
                totalSeconds = runningState.currentTimer.durationSeconds,
                modifier = Modifier.size(280.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = runningState.currentTimer.name,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
        
        // Bottom Section: Controls and overall progress
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Control Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (state is TimerState.Running && onPause != null) {
                    IconButton(
                        onClick = onPause,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                } else if (state is TimerState.Paused && onResume != null) {
                    IconButton(
                        onClick = onResume,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Resume",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                
                IconButton(
                    onClick = onSkip,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Skip",
                        modifier = Modifier.size(48.dp)
                    )
                }
                
                IconButton(
                    onClick = onStop,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Overall Progress
            OverallWorkoutProgress(
                progress = runningState.overallProgress
            )
        }
    }
}

@Composable
fun WorkoutCompletedScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Workout Complete!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Great job! You've completed your workout.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Done")
        }
    }
}
```

---

### Screen 4: Settings Screen

**Layout:**
```
┌─────────────────────────────────────┐
│ ←  Settings                         │
├─────────────────────────────────────┤
│                                     │
│ Display                             │
│ ┌─────────────────────────────────┐ │
│ │ Keep screen on          [✓]     │ │
│ └─────────────────────────────────┘ │
│ ┌─────────────────────────────────┐ │
│ │ Theme              System    ▼  │ │
│ └─────────────────────────────────┘ │
│                                     │
│ Audio                               │
│ ┌─────────────────────────────────┐ │
│ │ Text-to-Speech          [✓]     │ │
│ └─────────────────────────────────┘ │
│ ┌─────────────────────────────────┐ │
│ │ Sound effects           [✓]     │ │
│ └─────────────────────────────────┘ │
│ ┌─────────────────────────────────┐ │
│ │ Completion sound   Bell      ▼  │ │
│ └─────────────────────────────────┘ │
│ ┌─────────────────────────────────┐ │
│ │ Volume              ━━━━━━━━━○  │ │
│ └─────────────────────────────────┘ │
│                                     │
│ Haptics                             │
│ ┌─────────────────────────────────┐ │
│ │ Vibration               [✓]     │ │
│ └─────────────────────────────────┘ │
│                                     │
│ About                               │
│ Version 1.0.0                       │
│                                     │
└─────────────────────────────────────┘
```

**Compose Code:**
```kotlin
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                SettingsCategoryHeader("Display")
            }
            
            item {
                SwitchSetting(
                    title = "Keep screen on",
                    checked = settings.keepScreenOn,
                    onCheckedChange = { viewModel.updateKeepScreenOn(it) }
                )
            }
            
            item {
                DropdownSetting(
                    title = "Theme",
                    currentValue = settings.theme,
                    options = listOf("LIGHT", "DARK", "SYSTEM"),
                    onValueChange = { viewModel.updateTheme(it) }
                )
            }
            
            item {
                SettingsCategoryHeader("Audio")
            }
            
            item {
                SwitchSetting(
                    title = "Text-to-Speech",
                    checked = settings.enableTTS,
                    onCheckedChange = { viewModel.updateTTS(it) }
                )
            }
            
            item {
                SwitchSetting(
                    title = "Sound effects",
                    checked = settings.enableSoundEffects,
                    onCheckedChange = { viewModel.updateSoundEffects(it) }
                )
            }
            
            item {
                SoundPickerSetting(
                    title = "Completion sound",
                    currentSound = settings.completionSoundUri,
                    onSoundChange = { viewModel.updateCompletionSound(it) }
                )
            }
            
            item {
                SliderSetting(
                    title = "Volume",
                    value = settings.soundVolume,
                    onValueChange = { viewModel.updateVolume(it) },
                    enabled = settings.enableSoundEffects
                )
            }
            
            item {
                SettingsCategoryHeader("Haptics")
            }
            
            item {
                SwitchSetting(
                    title = "Vibration",
                    checked = settings.enableVibration,
                    onCheckedChange = { viewModel.updateVibration(it) }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            item {
                SettingsCategoryHeader("About")
            }
            
            item {
                ListItem(
                    headlineContent = { Text("Version") },
                    supportingContent = { Text("1.0.0") }
                )
            }
        }
    }
}

@Composable
private fun SettingsCategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun SwitchSetting(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = { Text(title) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        },
        modifier = modifier.clickable { onCheckedChange(!checked) }
    )
}

@Composable
private fun SliderSetting(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
```

---

## User Flows

### Flow 1: Create and Start New Workout

```
1. User opens app → Home Screen
2. User taps FAB (+) → Navigate to Create Workout Screen
3. User enters workout name: "Morning Flow"
4. User taps "Add Section" → Dialog appears
5. User enters section name: "Warm Up", repeats: 1 → Confirm
6. User taps "Add Timer" in Warm Up section → Dialog appears
7. User enters timer name: "Child's Pose", duration: 1:00 → Confirm
8. User taps "Add Timer" again → Dialog appears
9. User enters timer name: "Cat-Cow", duration: 0:30 → Confirm
10. User taps "Add Section" → Dialog appears
11. User enters section name: "Main Flow", repeats: 3 → Confirm
12. User taps "Add Section" inside Main Flow → Dialog appears
13. User enters section name: "Right Side", repeats: 2 → Confirm
14. User taps "Add Timer" in Right Side → Dialog appears
15. User enters timer name: "Warrior I", duration: 0:45 → Confirm
16. User taps Save (✓) → Workout saved to database
17. Navigate back to Home Screen → Workout appears in list
18. User taps on "Morning Flow" workout → Navigate to Workout Detail
19. User taps "Start Workout" → Navigate to Active Timer Screen
20. Timer starts automatically
21. TTS announces: "Warm Up. Child's Pose"
22. Timer counts down from 1:00
23. At 0:00, bell sound plays
24. Automatically advances to next timer
25. TTS announces: "Cat-Cow"
26. Timer counts down from 0:30
27. At 0:00, bell sound plays
28. TTS announces: "Main Flow. Repeat 3 times. Right Side. Repeat 2 times. Warrior I"
29. Progress shows: "Repeat 1 of 3" (Main Flow) and "Repeat 1 of 2" (Right Side)
30. Timer continues through all exercises
31. User can pause/skip/stop at any time
32. When all timers complete → Workout Completed Screen
33. User taps "Done" → Navigate back to Home
```

### Flow 2: Edit Existing Workout

```
1. User on Home Screen
2. User long-presses workout card → Context menu appears
3. User taps "Edit" → Navigate to Edit Workout Screen (pre-populated)
4. User modifies section name
5. User reorders timers via drag handles
6. User changes repeat count for section
7. User taps Save → Changes saved to database
8. Navigate back to Home Screen
```

### Flow 3: Background Timer Execution

```
1. User starts workout → Active Timer Screen
2. Timer running, showing 5:30 remaining
3. User presses home button → App goes to background
4. Notification appears on lock screen showing:
   - Section: "Main Flow"
   - Timer: "Plank"
   - Time: 5:29
   - Controls: Pause | Skip | Stop
5. Lock screen updates every second
6. User swipes notification → Opens app to Active Timer Screen
7. Timer still running, synchronized with notification
```

### Flow 4: Configure Settings

```
1. User on Home Screen
2. User taps Settings icon → Navigate to Settings Screen
3. User toggles "Keep screen on" to enabled
4. User changes Theme to "Dark"
5. User disables "Text-to-Speech"
6. User taps "Completion sound" dropdown
7. User selects "Bell" from system sounds
8. User adjusts volume slider to 80%
9. Settings auto-save on change
10. User taps back → Navigate to Home Screen
11. Next workout respects new settings
```

---

## Technical Implementation Details

### 1. Dependency Injection Setup

```kotlin
// di/DatabaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideYogaTimerDatabase(
        @ApplicationContext context: Context
    ): YogaTimerDatabase {
        return Room.databaseBuilder(
            context,
            YogaTimerDatabase::class.java,
            "yoga_timer_db"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Prepopulate with example workouts
                }
            })
            .build()
    }
    
    @Provides
    fun provideWorkoutDao(database: YogaTimerDatabase) = database.workoutDao()
    
    @Provides
    fun provideSectionDao(database: YogaTimerDatabase) = database.sectionDao()
    
    @Provides
    fun provideTimerDao(database: YogaTimerDatabase) = database.timerDao()
    
    @Provides
    fun provideSettingsDao(database: YogaTimerDatabase) = database.settingsDao()
}

// di/RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideWorkoutRepository(
        workoutDao: WorkoutDao,
        sectionDao: SectionDao,
        timerDao: TimerDao
    ): WorkoutRepository {
        return WorkoutRepositoryImpl(workoutDao, sectionDao, timerDao)
    }
    
    @Provides
    @Singleton
    fun provideSettingsRepository(
        settingsDao: SettingsDao
    ): SettingsRepository {
        return SettingsRepositoryImpl(settingsDao)
    }
}

// di/ServiceModule.kt
@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    
    @Provides
    @Singleton
    fun provideTimerManager(
        ttsManager: TTSManager,
        audioManager: AudioManager,
        notificationHelper: TimerNotificationHelper
    ): TimerManager {
        return TimerManager(ttsManager, audioManager, notificationHelper)
    }
    
    @Provides
    @Singleton
    fun provideTTSManager(
        @ApplicationContext context: Context
    ): TTSManager {
        return TTSManager(context)
    }
    
    @Provides
    @Singleton
    fun provideAudioManager(
        @ApplicationContext context: Context,
        settingsRepository: SettingsRepository
    ): AudioManager {
        return AudioManager(context, settingsRepository)
    }
}
```

### 2. Repository Implementation

```kotlin
// data/repository/WorkoutRepositoryImpl.kt
class WorkoutRepositoryImpl(
    private val workoutDao: WorkoutDao,
    private val sectionDao: SectionDao,
    private val timerDao: TimerDao
) : WorkoutRepository {
    
    override fun getAllWorkouts(): Flow<List<Workout>> {
        return workoutDao.getAllWorkouts().map { entities ->
            entities.map { entity ->
                entity.toDomainModel(
                    sections = loadSectionsForWorkout(entity.id)
                )
            }
        }
    }
    
    override suspend fun getWorkoutById(id: Long): Workout? {
        val entity = workoutDao.getWorkoutById(id) ?: return null
        val sections = loadSectionsForWorkout(id)
        return entity.toDomainModel(sections)
    }
    
    override suspend fun createWorkout(workout: Workout): Long {
        // 1. Insert workout
        val workoutId = workoutDao.insertWorkout(workout.toEntity())
        
        // 2. Insert sections and timers recursively
        workout.sections.forEachIndexed { index, section ->
            insertSectionRecursively(section.copy(workoutId = workoutId, sortOrder = index), null)
        }
        
        return workoutId
    }
    
    override suspend fun updateWorkout(workout: Workout) {
        workoutDao.updateWorkout(workout.toEntity())
        
        // Delete existing sections and recreate
        sectionDao.deleteSectionsForWorkout(workout.id)
        workout.sections.forEachIndexed { index, section ->
            insertSectionRecursively(section.copy(sortOrder = index), null)
        }
    }
    
    override suspend fun deleteWorkout(workoutId: Long) {
        workoutDao.getWorkoutById(workoutId)?.let {
            workoutDao.deleteWorkout(it)
        }
    }
    
    private suspend fun insertSectionRecursively(section: Section, parentId: Long?) {
        val sectionId = sectionDao.insertSection(
            section.toEntity().copy(parentSectionId = parentId)
        )
        
        // Insert timers
        section.timers.forEachIndexed { index, timer ->
            timerDao.insertTimer(
                timer.toEntity().copy(
                    sectionId = sectionId,
                    sortOrder = index
                )
            )
        }
        
        // Insert child sections recursively
        section.childSections.forEachIndexed { index, childSection ->
            insertSectionRecursively(
                childSection.copy(sortOrder = index),
                parentId = sectionId
            )
        }
    }
    
    private suspend fun loadSectionsForWorkout(workoutId: Long): List<Section> {
        return sectionDao.getSectionsForWorkout(workoutId)
            .first()
            .filter { it.parentSectionId == null }
            .map { loadSectionRecursively(it) }
    }
    
    private suspend fun loadSectionRecursively(entity: SectionEntity): Section {
        val timers = timerDao.getTimersForSection(entity.id)
            .map { it.toDomainModel() }
        
        val childSections = sectionDao.getChildSections(entity.id)
            .map { loadSectionRecursively(it) }
        
        return entity.toDomainModel(timers, childSections)
    }
}

// Extension functions for mapping
fun WorkoutEntity.toDomainModel(sections: List<Section> = emptyList()) = Workout(
    id = id,
    name = name,
    description = description,
    sections = sections,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isPreloaded = isPreloaded
)

fun Workout.toEntity() = WorkoutEntity(
    id = id,
    name = name,
    description = description,
    createdAt = createdAt,
    updatedAt = System.currentTimeMillis(),
    isPreloaded = isPreloaded,
    sortOrder = 0
)

// Similar extension functions for Section and Timer
```

### 3. ViewModel Example

```kotlin
// presentation/screens/home/HomeViewModel.kt
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllWorkoutsUseCase: GetAllWorkoutsUseCase,
    private val deleteWorkoutUseCase: DeleteWorkoutUseCase
) : ViewModel() {
    
    val workouts: StateFlow<List<Workout>> = getAllWorkoutsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun deleteWorkout(workoutId: Long) {
        viewModelScope.launch {
            deleteWorkoutUseCase(workoutId)
        }
    }
}

// presentation/screens/workout/active/ActiveTimerViewModel.kt
@HiltViewModel
class ActiveTimerViewModel @Inject constructor(
    private val timerManager: TimerManager,
    private val getWorkoutByIdUseCase: GetWorkoutByIdUseCase,
    private val settingsRepository: SettingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val workoutId: Long = savedStateHandle["workoutId"] ?: 0L
    
    var workoutName by mutableStateOf("")
        private set
    
    val timerState: StateFlow<TimerState> = timerManager.timerState
    
    val settings: StateFlow<Settings> = settingsRepository.getSettings()
        .map { it ?: Settings() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Settings()
        )
    
    init {
        loadAndStartWorkout()
    }
    
    private fun loadAndStartWorkout() {
        viewModelScope.launch {
            val workout = getWorkoutByIdUseCase(workoutId)
            if (workout != null) {
                workoutName = workout.name
                timerManager.startWorkout(workout)
            }
        }
    }
    
    fun pauseWorkout() {
        timerManager.pause()
    }
    
    fun resumeWorkout() {
        timerManager.resume()
    }
    
    fun skipTimer() {
        timerManager.skipToNext()
    }
    
    fun stopWorkout() {
        timerManager.stop()
    }
    
    override fun onCleared() {
        super.onCleared()
        timerManager.stop()
    }
}
```

### 4. Navigation Setup

```kotlin
// presentation/navigation/NavGraph.kt
@Composable
fun YogaTimerNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToCreate = { navController.navigate("create_workout") },
                onNavigateToSettings = { navController.navigate("settings") },
                onWorkoutClick = { workoutId ->
                    navController.navigate("active_timer/$workoutId")
                }
            )
        }
        
        composable("create_workout") {
            CreateWorkoutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "edit_workout/{workoutId}",
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
        ) {
            CreateWorkoutScreen(
                workoutId = it.arguments?.getLong("workoutId"),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "active_timer/{workoutId}",
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
        ) {
            ActiveTimerScreen(
                workoutId = it.arguments?.getLong("workoutId") ?: 0L,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
```

---

## Acceptance Criteria

### Epic 1: Workout Management
- [ ] User can create workout with name and description
- [ ] User can add sections with names, descriptions, and repeat counts
- [ ] User can nest sections up to 2 levels deep
- [ ] User can add timers with names, descriptions, and durations
- [ ] User can reorder sections and timers
- [ ] User can edit existing workouts
- [ ] User can delete workouts with confirmation
- [ ] Workouts persist across app restarts
- [ ] Pre-loaded example workouts appear on first launch

### Epic 2: Timer Execution
- [ ] Timers countdown accurately (±100ms tolerance)
- [ ] Automatic progression to next timer without user action
- [ ] TTS announces section name + description when section starts (if enabled)
- [ ] TTS announces timer description when timer starts (if enabled and description exists)
- [ ] Bell sound plays at end of each section repeat (configurable)
- [ ] Pause button freezes countdown
- [ ] Resume button continues from paused time
- [ ] Skip button immediately advances to next timer
- [ ] Stop button ends workout and returns to home
- [ ] Timers continue running in background
- [ ] App wake from background shows correct timer state

### Epic 3: Progress Visualization
- [ ] Circular progress indicator shows current timer countdown
- [ ] Time displays in MM:SS format
- [ ] Current timer name visible below circle
- [ ] Section name displays at top
- [ ] For sections with repeats: "Repeat X of Y" shown
- [ ] Dual progress bar shows total progress (darker) and current repeat progress (lighter)
- [ ] Overall progress shows "Exercise X of Y"
- [ ] Overall progress shows elapsed time / total time
- [ ] All progress indicators update every second
- [ ] UI remains responsive during countdown

### Epic 4: Lock Screen & Notifications
- [ ] Notification appears when timer starts
- [ ] Notification shows section name, timer name, remaining time
- [ ] Notification shows "Exercise X of Y"
- [ ] Notification updates every second
- [ ] Pause button on notification works
- [ ] Resume button on notification works
- [ ] Skip button on notification works
- [ ] Stop button on notification works
- [ ] Tapping notification opens app to Active Timer Screen
- [ ] Notification persists in background
- [ ] Notification dismissed when workout completes

### Epic 5: Settings
- [ ] Keep screen on toggle prevents screen timeout during workout
- [ ] Theme selection (Light/Dark/System) applies immediately
- [ ] TTS toggle enables/disables voice announcements
- [ ] Sound effects toggle enables/disables completion sounds
- [ ] Completion sound picker shows system sounds
- [ ] Selected sound plays preview when chosen
- [ ] Volume slider adjusts playback volume (0-100%)
- [ ] Vibration toggle enables/disables haptic feedback
- [ ] All settings persist across app restarts

### Epic 6: Audio & Haptics
- [ ] Completion bell sound plays at correct volume
- [ ] TTS voice is clear and understandable
- [ ] TTS speaks at appropriate pace
- [ ] Vibration triggers on timer completion (if enabled)
- [ ] Audio doesn't conflict with background music
- [ ] Sounds respect device silent mode

### Epic 7: Error Handling & Edge Cases
- [ ] App handles phone calls gracefully (pauses timer)
- [ ] App handles low battery gracefully
- [ ] App handles device restart (workout state lost, expected)
- [ ] App handles invalid durations (validates input)
- [ ] App handles empty workout names (shows error)
- [ ] App handles database corruption (fallback to empty state)
- [ ] App shows loading states during data operations

### Epic 8: Performance
- [ ] App launches in <2 seconds
- [ ] Workout list loads in <1 second
- [ ] Creating workout saves in <500ms
- [ ] Timer countdown has <100ms jitter
- [ ] UI animations run at 60fps
- [ ] Memory usage stays <100MB during workout
- [ ] Battery drain <5% per hour during active workout

---

## Testing Strategy

### Unit Tests
```kotlin
// Test example
class TimerManagerTest {
    
    @Test
    fun `when timer starts, state should be Running`() = runTest {
        val timerManager = TimerManager(mockTTS, mockAudio, mockNotification)
        val workout = createTestWorkout()
        
        timerManager.startWorkout(workout)
        
        val state = timerManager.timerState.value
        assertTrue(state is TimerState.Running)
    }
    
    @Test
    fun `when timer completes, should advance to next timer`() = runTest {
        // Test auto-progression logic
    }
    
    @Test
    fun `when section has repeats, should repeat correct number of times`() = runTest {
        // Test repeat logic
    }
}
```

### Integration Tests
```kotlin
@Test
fun `create workout end-to-end test`() {
    // Test full workflow: create -> save -> load -> start
}
```

### UI Tests
```kotlin
@Test
fun `user can create workout with section and timer`() {
    composeTestRule.onNodeWithText("Create Workout").performClick()
    composeTestRule.onNodeWithText("Workout Name").performTextInput("Test")
    composeTestRule.onNodeWithText("Add Section").performClick()
    // ... continue test
}
```

---

## Pre-loaded Example Workouts

### Example 1: Beginner Yoga
```kotlin
Workout(
    name = "Beginner Yoga Flow",
    description = "Gentle introduction to yoga poses",
    sections = listOf(
        Section(
            name = "Warm Up",
            description = "Prepare your body",
            repeatCount = 1,
            timers = listOf(
                Timer(name = "Child's Pose", durationSeconds = 60),
                Timer(name = "Cat-Cow", durationSeconds = 30),
                Timer(name = "Downward Dog", durationSeconds = 45)
            )
        ),
        Section(
            name = "Standing Poses",
            description = "Build strength and balance",
            repeatCount = 1,
            timers = listOf(
                Timer(name = "Mountain Pose", durationSeconds = 30),
                Timer(name = "Forward Fold", durationSeconds = 45),
                Timer(name = "Tree Pose (Right)", durationSeconds = 30),
                Timer(name = "Tree Pose (Left)", durationSeconds = 30)
            )
        ),
        Section(
            name = "Cool Down",
            repeatCount = 1,
            timers = listOf(
                Timer(name = "Seated Twist", durationSeconds = 30),
                Timer(name = "Corpse Pose", durationSeconds = 120)
            )
        )
    )
)
```

### Example 2: Advanced Flow with Repeats
```kotlin
Workout(
    name = "Advanced Vinyasa",
    description = "Dynamic flow for experienced practitioners",
    sections = listOf(
        Section(
            name = "Sun Salutations",
            description = "Warm up with sun salutations",
            repeatCount = 3,
            timers = listOf(
                Timer(name = "Forward Fold", durationSeconds = 15),
                Timer(name = "Plank", durationSeconds = 20),
                Timer(name = "Chaturanga", durationSeconds = 10),
                Timer(name = "Upward Dog", durationSeconds = 15),
                Timer(name = "Downward Dog", durationSeconds = 20)
            )
        ),
        Section(
            name = "Warrior Flow",
            description = "Build strength and stamina",
            repeatCount = 2,
            childSections = listOf(
                Section(
                    name = "Right Side",
                    repeatCount = 1,
                    timers = listOf(
                        Timer(name = "Warrior I", durationSeconds = 45),
                        Timer(name = "Warrior II", durationSeconds = 45),
                        Timer(name = "Triangle", durationSeconds = 30)
                    )
                ),
                Section(
                    name = "Left Side",
                    repeatCount = 1,
                    timers = listOf(
                        Timer(name = "Warrior I", durationSeconds = 45),
                        Timer(name = "Warrior II", durationSeconds = 45),
                        Timer(name = "Triangle", durationSeconds = 30)
                    )
                )
            )
        ),
        Section(
            name = "Cool Down",
            repeatCount = 1,
            timers = listOf(
                Timer(name = "Pigeon Pose", durationSeconds = 60),
                Timer(name = "Savasana", durationSeconds = 180)
            )
        )
    )
)
```

---

## Development Milestones

### Sprint 1 (Weeks 1-2): Foundation
- [ ] Project setup with Hilt, Room, Compose
- [ ] Database schema implementation
- [ ] Repository layer implementation
- [ ] Basic navigation structure
- [ ] Home screen with workout list

### Sprint 2 (Weeks 3-4): Workout Creation
- [ ] Create/Edit workout screen UI
- [ ] Section and timer creation dialogs
- [ ] Nested section support
- [ ] Reordering functionality
- [ ] Save/load workouts

### Sprint 3 (Weeks 5-6): Timer Engine
- [ ] Timer Manager implementation
- [ ] State machine for timer states
- [ ] Countdown logic with Coroutines
- [ ] Auto-progression logic
- [ ] Repeat handling

### Sprint 4 (Weeks 7-8): Active Timer UI
- [ ] Active timer screen layout
- [ ] Circular progress indicator
- [ ] Progress bars (section + overall)
- [ ] Control buttons (pause/skip/stop)
- [ ] Real-time updates

### Sprint 5 (Weeks 9-10): Audio & Feedback
- [ ] TTS integration
- [ ] Audio manager for completion sounds
- [ ] Haptic feedback
- [ ] Volume controls
- [ ] Settings screen

### Sprint 6 (Weeks 11-12): Background & Notifications
- [ ] Foreground service
- [ ] Notification with media controls
- [ ] Lock screen display
- [ ] Background timer continuation
- [ ] WorkManager integration

### Sprint 7 (Weeks 13-14): Polish & Testing
- [ ] Pre-loaded example workouts
- [ ] Error handling
- [ ] Loading states
- [ ] Unit tests
- [ ] UI tests
- [ ] Bug fixes

### Sprint 8 (Weeks 15-16): Final QA & Release
- [ ] Integration testing
- [ ] Performance optimization
- [ ] Documentation
- [ ] Release build configuration
- [ ] Play Store preparation

---

## Claude Code Specific Instructions

### When Starting Development:

1. **Read this document thoroughly** - All requirements are here
2. **Start with database** - Implement entities, DAOs, and database class first
3. **Build from bottom up** - Repository → Use Cases → ViewModels → UI
4. **Test incrementally** - Write unit tests as you implement features
5. **Follow Material 3 guidelines** - Use MaterialTheme colors and components
6. **Handle errors gracefully** - Add try-catch blocks and show user-friendly messages
7. **Log important events** - Use Timber for logging
8. **Comment complex logic** - Especially timer state management and progress calculations

### Code Quality Standards:

- Use meaningful variable names
- Keep functions under 30 lines when possible
- Extract complex logic into separate functions
- Use Kotlin coroutines for async operations (no callbacks)
- Follow single responsibility principle
- Use sealed classes for states
- Use data classes for models
- Avoid magic numbers - use constants

### Git Commit Messages:

- Format: `[Component] Brief description`
- Examples:
  - `[Database] Add workout entities and DAOs`
  - `[UI] Implement active timer screen`
  - `[Feature] Add TTS announcements`
  - `[Fix] Correct timer progression logic`

---

## Questions to Ask PM Before Starting:

1. Should workout names be unique?
2. Maximum nesting depth for sections?
3. Maximum repeat count for sections?
4. Should we support workout sharing/export?
5. Analytics integration needed?
6. Crash reporting tool preference?
7. Should timers support milliseconds or just seconds?
8. Minimum timer duration allowed?
9. Maximum timer duration allowed?
10. Should we support custom TTS voices?

---

## End of Document

This specification provides everything Claude Code needs to build the Android Yoga Timer app. All requirements, technical details, UI specifications, and acceptance criteria are documented. Good luck with development!
