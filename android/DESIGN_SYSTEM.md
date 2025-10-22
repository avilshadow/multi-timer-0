# Yoga Timer - Design System & Visual Specifications
## Android App - Material Design 3

**Version:** 1.0  
**Last Updated:** 2025  
**Design System:** Material Design 3  
**Target Platform:** Android 8.0+ (API 26+)

---

## Table of Contents
1. [Design Philosophy](#design-philosophy)
2. [Color System](#color-system)
3. [Typography](#typography)
4. [Spacing & Layout](#spacing--layout)
5. [Components Library](#components-library)
6. [Icon System](#icon-system)
7. [Screen Designs](#screen-designs)
8. [Animations & Transitions](#animations--transitions)
9. [Accessibility](#accessibility)
10. [Implementation Guide](#implementation-guide)

---

## Design Philosophy

### Core Principles
1. **Clarity** - Information hierarchy is clear and scannable
2. **Simplicity** - Minimal UI during active workouts to avoid distraction
3. **Feedback** - Immediate visual/audio response to all user actions
4. **Calmness** - Soft colors and smooth animations for meditative experience
5. **Focus** - Large, readable text and controls for eyes-closed yoga practice

### Use Case Considerations
- Users may have eyes closed during practice
- Users need glanceable information
- Minimal interaction required during workout
- Lock screen must show essential info
- Audio cues are primary, visual is secondary

---

## Color System

### Material Design 3 Color Scheme

#### Light Theme
```kotlin
// colors/LightColors.kt
val md_theme_light_primary = Color(0xFF6750A4)           // Purple (primary brand)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)        // White text on primary
val md_theme_light_primaryContainer = Color(0xFFEADDFF) // Light purple container
val md_theme_light_onPrimaryContainer = Color(0xFF21005D)

val md_theme_light_secondary = Color(0xFF625B71)        // Muted purple-gray
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFE8DEF8)
val md_theme_light_onSecondaryContainer = Color(0xFF1D192B)

val md_theme_light_tertiary = Color(0xFF7D5260)         // Mauve accent
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFFFD8E4)
val md_theme_light_onTertiaryContainer = Color(0xFF31111D)

val md_theme_light_error = Color(0xFFB3261E)            // Error red
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFF9DEDC)
val md_theme_light_onErrorContainer = Color(0xFF410E0B)

val md_theme_light_background = Color(0xFFFFFBFE)       // Off-white
val md_theme_light_onBackground = Color(0xFF1C1B1F)
val md_theme_light_surface = Color(0xFFFFFBFE)
val md_theme_light_onSurface = Color(0xFF1C1B1F)
val md_theme_light_surfaceVariant = Color(0xFFE7E0EC)
val md_theme_light_onSurfaceVariant = Color(0xFF49454F)

val md_theme_light_outline = Color(0xFF79747E)
val md_theme_light_outlineVariant = Color(0xFFCAC4D0)
val md_theme_light_scrim = Color(0xFF000000)
```

#### Dark Theme
```kotlin
// colors/DarkColors.kt
val md_theme_dark_primary = Color(0xFFD0BCFF)           // Light purple
val md_theme_dark_onPrimary = Color(0xFF381E72)         // Dark purple text
val md_theme_dark_primaryContainer = Color(0xFF4F378B)  // Medium purple container
val md_theme_dark_onPrimaryContainer = Color(0xFFEADDFF)

val md_theme_dark_secondary = Color(0xFFCCC2DC)         // Light gray-purple
val md_theme_dark_onSecondary = Color(0xFF332D41)
val md_theme_dark_secondaryContainer = Color(0xFF4A4458)
val md_theme_dark_onSecondaryContainer = Color(0xFFE8DEF8)

val md_theme_dark_tertiary = Color(0xFFEFB8C8)          // Light pink
val md_theme_dark_onTertiary = Color(0xFF492532)
val md_theme_dark_tertiaryContainer = Color(0xFF633B48)
val md_theme_dark_onTertiaryContainer = Color(0xFFFFD8E4)

val md_theme_dark_error = Color(0xFFF2B8B5)
val md_theme_dark_onError = Color(0xFF601410)
val md_theme_dark_errorContainer = Color(0xFF8C1D18)
val md_theme_dark_onErrorContainer = Color(0xFFF9DEDC)

val md_theme_dark_background = Color(0xFF1C1B1F)        // Very dark gray
val md_theme_dark_onBackground = Color(0xFFE6E1E5)
val md_theme_dark_surface = Color(0xFF1C1B1F)
val md_theme_dark_onSurface = Color(0xFFE6E1E5)
val md_theme_dark_surfaceVariant = Color(0xFF49454F)
val md_theme_dark_onSurfaceVariant = Color(0xFFCAC4D0)

val md_theme_dark_outline = Color(0xFF938F99)
val md_theme_dark_outlineVariant = Color(0xFF49454F)
val md_theme_dark_scrim = Color(0xFF000000)
```

### Semantic Colors
```kotlin
// Workout-specific colors
val timer_active = Color(0xFF6750A4)        // Primary - running timer
val timer_paused = Color(0xFF625B71)        // Secondary - paused state
val timer_complete = Color(0xFF4CAF50)      // Green - completion
val progress_total = Color(0xFF9E8FB5)      // Darker purple - total progress
val progress_current = Color(0xFFD0BCFF)    // Lighter purple - current progress
```

### Color Usage Guidelines

**Primary Color (Purple)**
- Main action buttons
- Active timer circle
- Navigation highlights
- FAB (Floating Action Button)

**Secondary Color (Gray-Purple)**
- Secondary actions
- Paused state indicators
- Section headers

**Surface Colors**
- Cards: `surfaceVariant` with 2dp elevation
- Dialogs: `surface` with 6dp elevation
- Sheets: `surface` with 1dp elevation

---

## Typography

### Type Scale (Material Design 3)

```kotlin
// Type scale using Roboto font family
val Typography = Typography(
    // Display styles
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    
    // Headline styles
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    
    // Title styles
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    // Body styles
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    
    // Label styles
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
```

### Typography Usage Map

| Screen Element | Typography Style | Use Case |
|---------------|------------------|----------|
| Timer countdown | displayLarge (72sp custom) | Main countdown display |
| Section name | headlineMedium | Active timer section header |
| Timer name | titleLarge | Current exercise name |
| Workout card title | titleLarge | Home screen workout names |
| Card subtitle | bodyMedium | Exercise count, duration |
| Progress text | bodyMedium | "Repeat 2 of 3", "Exercise 5 of 12" |
| Button labels | labelLarge | All button text |
| Settings labels | bodyLarge | Setting names |
| Supporting text | bodySmall | Descriptions, hints |

### Font Weights
- **Light (300)** - Timer countdown only
- **Regular (400)** - Body text, descriptions
- **Medium (500)** - Titles, labels, buttons
- **Bold (700)** - Section headers, emphasis

---

## Spacing & Layout

### Spacing Scale
```kotlin
// Standard spacing units
val space_xs = 4.dp
val space_sm = 8.dp
val space_md = 16.dp
val space_lg = 24.dp
val space_xl = 32.dp
val space_xxl = 48.dp
val space_xxxl = 64.dp
```

### Grid System
- Base unit: 8dp
- Content margins: 16dp (mobile), 24dp (tablet)
- Card padding: 16dp
- List item padding: 16dp horizontal, 12dp vertical
- Section spacing: 24dp between sections

### Screen Margins
```
Mobile Portrait (< 600dp):
  - Side margins: 16dp
  - Top/bottom safe area: 8dp
  
Tablet (≥ 600dp):
  - Side margins: 24dp
  - Max content width: 840dp
  - Center content on wider screens
```

### Touch Targets
- Minimum: 48dp × 48dp (Material Design requirement)
- Recommended: 56dp × 56dp for primary actions
- Icon buttons: 48dp × 48dp
- FAB: 56dp × 56dp (standard), 96dp × 96dp (extended)

---

## Components Library

### 1. Workout Card
```
┌─────────────────────────────────────────┐
│  Morning Flow                    ⋮      │ ← titleLarge, Bold
│  5 exercises · 15 min                   │ ← bodyMedium, onSurfaceVariant
│  ━━━━━━━━━━━░░░░░░ 60%                 │ ← 4dp height progress
└─────────────────────────────────────────┘

Specifications:
- Height: wrap_content (min 88dp)
- Corner radius: 12dp
- Elevation: 2dp (light), 1dp (dark)
- Padding: 16dp all sides
- Background: surfaceVariant
- Ripple effect on tap
- Long-press shows context menu (Edit/Delete)

Progress bar (if in progress):
- Height: 4dp
- Corner radius: 2dp
- Color: primary (filled), surfaceVariant (track)
- Margin top: 8dp
```

**Compose Implementation:**
```kotlin
@Composable
fun WorkoutCard(
    workout: Workout,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(
                    onClick = onLongClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${workout.exerciseCount} exercises · ${workout.duration / 60} min",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (workout.progress > 0f) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { workout.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}
```

---

### 2. Circular Timer Progress

```
        ┌─────────────┐
       ╱   ┌─────┐   ╲
      │    │     │    │
      │    │1:45 │    │  ← displayLarge (72sp), Light weight
      │    │     │    │
       ╲   └─────┘   ╱
        └─────────────┘

Specifications:
- Diameter: 280dp
- Stroke width: 12dp
- Background circle: surfaceVariant
- Progress circle: primary
- Cap: Round
- Direction: Clockwise from top
- Center text: countdown in MM:SS

Animation:
- Smooth 1-second transitions
- Easing: LinearEasing (consistent countdown)
- No bounce or overshoot
```

**Compose Implementation:**
```kotlin
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
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.LightGray.copy(alpha = 0.3f),
                radius = size.minDimension / 2,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        
        // Progress circle
        val progress = 1f - (remainingSeconds.toFloat() / totalSeconds.toFloat())
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = MaterialTheme.colorScheme.primary,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        
        // Countdown text
        Text(
            text = formatTime(remainingSeconds),
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 72.sp,
                fontWeight = FontWeight.Light
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%d:%02d".format(mins, secs)
}
```

---

### 3. Section Repeat Progress Bar

```
Repeat 2 of 3                      ← titleMedium, Medium weight
━━━━━━━━━━━━░░░░░░░░              ← Dual-layer progress

Specifications:
- Width: 80% of screen width
- Height: 12dp
- Corner radius: 6dp
- Two layers:
  Layer 1 (bottom): Total progress
    - Color: primaryContainer (darker)
    - Shows completed repeats
  Layer 2 (top): Current repeat progress
    - Color: primary (lighter)
    - Shows progress within current repeat
- Label above: "Repeat X of Y"
  - Typography: titleMedium
  - Margin bottom: 8dp
```

**Compose Implementation:**
```kotlin
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
            // Total progress (darker background)
            val totalProgress = (currentRepeat - 1).toFloat() / totalRepeats.toFloat()
            LinearProgressIndicator(
                progress = { totalProgress },
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(6.dp)),
                color = MaterialTheme.colorScheme.primaryContainer,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            // Current repeat progress (lighter overlay)
            val currentProgress = currentTimerIndex.toFloat() / totalTimers.toFloat()
            val incrementProgress = currentProgress / totalRepeats.toFloat()
            val combinedProgress = totalProgress + incrementProgress
            
            LinearProgressIndicator(
                progress = { combinedProgress },
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(6.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent
            )
        }
    }
}
```

---

### 4. Overall Progress Bar

```
Exercise 5 of 12          8:23 / 15:00
━━━━━━━━━░░░░░░░░░░░░░░░░░░░░

Specifications:
- Width: 80% of screen width
- Height: 8dp
- Corner radius: 4dp
- Color: secondary
- Track color: surfaceVariant
- Label row above:
  - Left: "Exercise X of Y" (bodyMedium)
  - Right: "elapsed / total" (bodyMedium)
  - Margin bottom: 8dp
```

**Compose Implementation:**
```kotlin
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Exercise $currentExercise of $totalExercises",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${formatTime(elapsedSeconds)} / ${formatTime(totalSeconds)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LinearProgressIndicator(
            progress = { currentExercise.toFloat() / totalExercises.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
```

---

### 5. Control Buttons (Active Timer)

```
    ⏸       ⏭       ⏹
  Pause    Skip    Stop

Specifications:
- Size: 64dp × 64dp touch target
- Icon size: 48dp × 48dp
- Spacing between: 24dp
- Color: onSurface
- Background: surface (subtle container)
- Ripple: bounded
- Elevation on press: 2dp → 4dp
```

**Compose Implementation:**
```kotlin
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
                containerColor = MaterialTheme.colorScheme.secondaryContainer
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
                containerColor = MaterialTheme.colorScheme.secondaryContainer
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
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "Stop",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}
```

---

### 6. Floating Action Button (FAB)

```
                                    ┌───┐
                                    │ + │
                                    └───┘

Specifications:
- Size: 56dp × 56dp
- Icon size: 24dp × 24dp
- Position: Bottom-right, 16dp from edges
- Elevation: 6dp
- Color: primaryContainer
- Icon color: onPrimaryContainer
- Shape: Circle (28dp radius)
```

---

### 7. Section Card (Edit Mode)

```
┌─────────────────────────────────────────┐
│ ≡  Main Flow (×3)              ︙  ✕    │
│                                          │
│    • Warrior I (0:45)                   │
│    • Warrior II (0:45)                  │
│                                          │
│    + Add Timer                          │
│    + Add Section                        │
└─────────────────────────────────────────┘

Specifications:
- Background: surfaceVariant
- Padding: 12dp
- Corner radius: 8dp
- Drag handle (≡): 20dp, onSurfaceVariant
- Section name: titleSmall, Bold
- Repeat badge: primary container, 20dp height
- Timer items: 4dp vertical padding
- Add buttons: TextButton style
```

---

## Icon System

### Material Icons (Filled)
All icons use Material Symbols from `androidx.compose.material.icons`

| Icon | Usage | Size |
|------|-------|------|
| `Icons.Default.Add` | Create new, Add item | 24dp |
| `Icons.Default.PlayArrow` | Resume workout | 48dp (controls) |
| `Icons.Default.Pause` | Pause workout | 48dp (controls) |
| `Icons.Default.Stop` | Stop workout | 48dp (controls) |
| `Icons.Default.SkipNext` | Skip timer | 48dp (controls) |
| `Icons.Default.Timer` | Timer indicator | 16dp (list) |
| `Icons.Default.Settings` | Settings menu | 24dp (toolbar) |
| `Icons.Default.MoreVert` | Context menu | 24dp |
| `Icons.Default.Edit` | Edit action | 18dp |
| `Icons.Default.Delete` | Delete action | 18dp |
| `Icons.Default.DragHandle` | Reorder handle | 20dp |
| `Icons.Default.CheckCircle` | Completion | 120dp (success) |
| `Icons.AutoMirrored.Filled.ArrowBack` | Navigation back | 24dp |
| `Icons.Default.Notifications` | Notification icon | 24dp (status bar) |

### Custom Icons (If needed)
If Material icons don't cover all needs, create SVG icons with:
- Stroke width: 2dp
- Viewbox: 24×24
- Fill: none (outline style)
- Export as vector drawable

---

## Screen Designs

### Screen 1: Home Screen (Light Theme)

```
┌─────────────────────────────────────────┐
│ ☰  Yoga Timer                 ⚙         │ ← Top App Bar (64dp height)
├─────────────────────────────────────────┤   Background: surface
│                                          │   Elevation: 0dp (no shadow)
│   ┌──────────────────────────────────┐  │
│   │ Morning Flow                  ⋮  │  │ ← Workout Card
│   │ 5 exercises · 15 min             │  │   surfaceVariant, 12dp radius
│   │ ━━━━━━━━━━━░░░░░░ 60%          │  │   16dp margin horizontal
│   └──────────────────────────────────┘  │   12dp margin between cards
│                                          │
│   ┌──────────────────────────────────┐  │
│   │ Evening Stretch               ⋮  │  │
│   │ 8 exercises · 20 min             │  │
│   │ Not started                      │  │
│   └──────────────────────────────────┘  │
│                                          │
│   ┌──────────────────────────────────┐  │
│   │ Power Yoga                    ⋮  │  │
│   │ 12 exercises · 30 min            │  │
│   │ Not started                      │  │
│   └──────────────────────────────────┘  │
│                                          │
│                                          │
│                                          │
│                                          │
│                                    ┌───┐ │ ← FAB
│                                    │ + │ │   56dp, 16dp from edges
│                                    └───┘ │
└─────────────────────────────────────────┘
   ↑                                    ↑
  16dp margin                        16dp margin

Empty State (no workouts):
┌─────────────────────────────────────────┐
│                                          │
│                                          │
│              🧘                          │ ← Icon (64dp)
│                                          │   onSurfaceVariant
│       No workouts yet                   │ ← titleLarge
│                                          │
│   Tap + to create your first workout    │ ← bodyMedium
│                                          │   onSurfaceVariant
│                                          │
│                                    ┌───┐ │
│                                    │ + │ │
│                                    └───┘ │
└─────────────────────────────────────────┘
```

**Color Specifications:**
- Status bar: surface (transparent on Android 12+)
- Top App Bar: surface
- Background: background
- Cards: surfaceVariant
- FAB: primaryContainer
- Text: onSurface, onSurfaceVariant

---

### Screen 2: Active Timer Screen (Light Theme)

```
┌─────────────────────────────────────────┐
│ ←  Morning Flow                          │ ← Top App Bar
├─────────────────────────────────────────┤   Back button: 48dp tap target
│                  24dp                    │
│         Main Flow                        │ ← Section name
│       Repeat 2 of 3                      │   headlineMedium, Bold
│   ━━━━━━━━━━━━░░░░░░░░                 │   Section repeat progress
│                  12dp                    │   12dp height bar
│                                          │
│        ┌─────────────┐                   │
│       ╱               ╲    280dp         │ ← Circular timer
│      │                 │                 │   12dp stroke
│      │                 │                 │   primary color
│      │      1:45       │   72sp          │   Countdown: displayLarge
│      │                 │   Light weight  │
│      │                 │                 │
│       ╲               ╱                  │
│        └─────────────┘                   │
│                  24dp                    │
│         Warrior I                        │ ← Timer name
│                                          │   titleLarge
│                  32dp                    │
│       ⏸      ⏭      ⏹                  │ ← Control buttons
│     Pause   Skip   Stop                 │   64dp each, 24dp spacing
│                                          │
│                  32dp                    │
│  Exercise 5 of 12      8:23 / 15:00    │ ← Overall progress
│  ━━━━━━━━░░░░░░░░░░░░░░░░░░░░░        │   secondary color
│                                          │   80% width
│                  24dp                    │
└─────────────────────────────────────────┘
```

**Layout Specifications:**
- All centered horizontally
- Vertical spacing as marked
- Safe area padding: 24dp top/bottom
- Content max-width: 360dp (centered on tablets)

**Color Specifications:**
- Section name: onSurface
- Repeat progress bar: primary + primaryContainer
- Timer circle progress: primary
- Timer countdown: onSurface
- Timer name: onSurface
- Control buttons: secondaryContainer (Pause/Skip), errorContainer (Stop)
- Progress text: onSurfaceVariant
- Progress bar: secondary

---

### Screen 3: Create Workout Screen

```
┌─────────────────────────────────────────┐
│ ←  Create Workout                    ✓  │ ← Top App Bar
├─────────────────────────────────────────┤   Save button (checkmark)
│                                          │
│ Workout Name                            │ ← Label (labelMedium)
│ ┌──────────────────────────────────────┐│   8dp below label
│ │ Morning Flow                         ││ ← TextField
│ └──────────────────────────────────────┘│   56dp height
│                  16dp                    │   outlined style
│                                          │
│ Description (optional)                   │
│ ┌──────────────────────────────────────┐│
│ │ Energizing flow to start the day    ││ ← Multiline TextField
│ │                                      ││   min 2 lines
│ └──────────────────────────────────────┘│
│                  24dp                    │
│                                          │
│ Sections                       + Section │ ← Header with action
│                  8dp                     │   titleMedium, Bold
│ ┌──────────────────────────────────────┐│
│ │ ≡  Warm Up                    ︙  ✕  ││ ← Section card
│ │                                       ││   surfaceVariant
│ │    • Child's Pose (1:00)             ││   12dp padding
│ │    • Cat-Cow (0:30)                  ││   8dp radius
│ │                                       ││
│ │    + Add Timer                       ││   Text button
│ └──────────────────────────────────────┘│
│                  8dp                     │
│ ┌──────────────────────────────────────┐│
│ │ ≡  Main Flow (×3)             ︙  ✕  ││ ← Section with repeat
│ │    └─ Right Side (×2)         ︙  ✕  ││   Nested section
│ │       • Warrior I (0:45)             ││   Indented 16dp
│ │       + Add Timer                    ││
│ │    + Add Section                     ││
│ └──────────────────────────────────────┘│
│                  16dp                    │
│ ┌──────────────────────────────────────┐│
│ │ + Add Section                        ││ ← Outlined button
│ └──────────────────────────────────────┘│   48dp height
│                                          │
└─────────────────────────────────────────┘
```

**Interactive Elements:**
- Drag handle (≡): Triggers reorder mode
- More menu (︙): Shows Edit/Delete options
- Delete (✕): Shows confirmation dialog
- + Add Timer: Opens timer input dialog
- + Add Section: Opens section input dialog

**Dialogs (Add Timer):**
```
┌─────────────────────────────────┐
│  Add Timer                      │ ← Dialog title (24dp padding)
├─────────────────────────────────┤
│                                 │
│  Timer Name                     │ ← TextField
│  ┌───────────────────────────┐ │   outlined
│  │ Warrior I                 │ │
│  └───────────────────────────┘ │
│           16dp                  │
│  Description (optional)         │
│  ┌───────────────────────────┐ │
│  │ Hold the pose             │ │
│  └───────────────────────────┘ │
│           16dp                  │
│  Duration                       │
│  ┌─────────┐  :  ┌─────────┐  │ ← Number pickers
│  │   0    │     │   45    │  │   Minutes : Seconds
│  └─────────┘     └─────────┘  │
│                                 │
│           24dp                  │
│         CANCEL      ADD        │ ← Text buttons
│                                 │   16dp padding bottom
└─────────────────────────────────┘
```

---

### Screen 4: Settings Screen

```
┌─────────────────────────────────────────┐
│ ←  Settings                              │
├─────────────────────────────────────────┤
│                                          │
│ DISPLAY                                 │ ← Category header
│ ┌──────────────────────────────────────┐│   labelSmall, primary
│ │ Keep screen on                     ☑││   8dp padding vertical
│ └──────────────────────────────────────┘│
│ ┌──────────────────────────────────────┐│
│ │ Theme                     System  ▼ ││ ← Dropdown
│ └──────────────────────────────────────┘│
│                                          │
│ AUDIO                                   │
│ ┌──────────────────────────────────────┐│
│ │ Text-to-Speech                     ☑││
│ └──────────────────────────────────────┘│
│ ┌──────────────────────────────────────┐│
│ │ Sound effects                      ☑││
│ └──────────────────────────────────────┘│
│ ┌──────────────────────────────────────┐│
│ │ Completion sound          Bell    ▼ ││
│ └──────────────────────────────────────┘│
│ ┌──────────────────────────────────────┐│
│ │ Volume                                ││
│ │ ━━━━━━━━━━━━━━━━━━━○               ││ ← Slider
│ └──────────────────────────────────────┘│   16dp padding
│                                          │
│ HAPTICS                                 │
│ ┌──────────────────────────────────────┐│
│ │ Vibration                          ☑││
│ └──────────────────────────────────────┘│
│                                          │
│ ABOUT                                   │
│ ┌──────────────────────────────────────┐│
│ │ Version                     1.0.0    ││ ← Non-interactive
│ └──────────────────────────────────────┘│   bodyMedium
│                                          │
└─────────────────────────────────────────┘
```

**Setting Item Specifications:**
- Height: 56dp (single line), 72dp (two line)
- Padding: 16dp horizontal
- Divider: 1dp, outlineVariant
- Switch: 48dp × 48dp touch target
- Slider: 48dp height touch target, 4dp track height

---

## Animations & Transitions

### Screen Transitions
```kotlin
// Navigation transitions (Material Motion)
val slideIntoContainer = slideIntoContainer(
    towards = AnimatedContentTransitionScope.SlideDirection.Start,
    animationSpec = tween(300, easing = FastOutSlowInEasing)
)

val slideOutOfContainer = slideOutOfContainer(
    towards = AnimatedContentTransitionScope.SlideDirection.Start,
    animationSpec = tween(300, easing = FastOutSlowInEasing)
)
```

### Timer Countdown Animation
```kotlin
// Smooth countdown update
val animatedProgress by animateFloatAsState(
    targetValue = currentProgress,
    animationSpec = tween(
        durationMillis = 1000,
        easing = LinearEasing
    ),
    label = "timer_progress"
)
```

### Card Appearance
```kotlin
// Staggered fade-in for workout cards
items.forEachIndexed { index, item ->
    Card(
        modifier = Modifier
            .animateContentSize()
            .graphicsLayer {
                alpha = animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = index * 50
                    )
                ).value
            }
    )
}
```

### Button Press States
- **Resting:** Scale 1.0
- **Pressed:** Scale 0.95, duration 100ms
- **Released:** Scale 1.0, duration 150ms, overshoot

### Progress Bar Animations
- **Linear update:** 1000ms duration (matches 1-second countdown)
- **Easing:** LinearEasing (no acceleration)
- **No bounce:** Keep updates smooth and predictable

---

## Accessibility

### Touch Targets
✅ All interactive elements: minimum 48dp × 48dp
✅ Primary actions: 56dp × 56dp recommended
✅ Spacing between targets: minimum 8dp

### Color Contrast
✅ Text on background: minimum 4.5:1 ratio (WCAG AA)
✅ Large text (18sp+): minimum 3:1 ratio
✅ Icons: minimum 3:1 ratio

### Text Sizes
✅ Minimum body text: 14sp
✅ Support dynamic type (user font size settings)
✅ Test at 200% font scale

### Screen Reader Support
```kotlin
// Add content descriptions
Icon(
    imageVector = Icons.Default.PlayArrow,
    contentDescription = "Resume workout"
)

// Semantic labels for complex UI
modifier = Modifier.semantics {
    contentDescription = "Timer: 1 minute 45 seconds remaining"
    role = Role.Button
}
```

### Focus Order
- Top-to-bottom, left-to-right
- Skip navigation links at top
- Focus visible indicator: 2dp outline, primary color

### Motion Sensitivity
```kotlin
// Respect user's motion preferences
val isReduceMotionEnabled = LocalAccessibilityManager.current.isReduceMotionEnabled

val animationSpec = if (isReduceMotionEnabled) {
    snap() // Instant, no animation
} else {
    tween(300) // Normal animation
}
```

---

## Implementation Guide

### Theme Setup

```kotlin
// theme/Theme.kt
@Composable
fun YogaTimerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

### Shape Configuration
```kotlin
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // Chips, badges
    small = RoundedCornerShape(8.dp),        // Buttons, progress bars
    medium = RoundedCornerShape(12.dp),      // Cards
    large = RoundedCornerShape(16.dp),       // Dialogs, sheets
    extraLarge = RoundedCornerShape(28.dp)   // FAB
)
```

### Elevation Tokens
```kotlin
object Elevation {
    val level0 = 0.dp     // Surface
    val level1 = 1.dp     // Cards in dark mode
    val level2 = 2.dp     // Cards in light mode
    val level3 = 6.dp     // FAB, menus
    val level4 = 8.dp     // Navigation drawer
    val level5 = 12.dp    // Dialogs
}
```

### Testing Designs
```kotlin
// Preview with light theme
@Preview(name = "Light Mode", showBackground = true)
@Composable
fun HomeScreenPreviewLight() {
    YogaTimerTheme(darkTheme = false) {
        HomeScreen(/*...*/)
    }
}

// Preview with dark theme
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreviewDark() {
    YogaTimerTheme(darkTheme = true) {
        HomeScreen(/*...*/)
    }
}

// Preview with large font
@Preview(name = "Large Font", showBackground = true, fontScale = 1.5f)
@Composable
fun HomeScreenPreviewLargeFont() {
    YogaTimerTheme {
        HomeScreen(/*...*/)
    }
}
```

---

## Design Checklist for Claude Code

### Before Starting
- [ ] Read entire design system document
- [ ] Understand Material Design 3 principles
- [ ] Review color tokens and usage
- [ ] Study typography scale
- [ ] Check accessibility requirements

### During Implementation
- [ ] Use semantic color tokens (not hardcoded colors)
- [ ] Apply correct typography styles to text
- [ ] Maintain 8dp spacing grid
- [ ] Ensure 48dp minimum touch targets
- [ ] Add content descriptions for accessibility
- [ ] Test with both light and dark themes
- [ ] Preview at different font scales
- [ ] Verify color contrast ratios

### Before Committing
- [ ] All screens match design specifications
- [ ] Animations are smooth (60fps)
- [ ] No hardcoded dimensions (use spacing tokens)
- [ ] Accessibility labels present
- [ ] Previews included for all composables
- [ ] Dark mode tested
- [ ] Large font tested
- [ ] Touch targets verified

---

## Design Assets Needed

### To be provided by designer (if available):
1. **App Icon**
   - Adaptive icon (foreground + background)
   - Sizes: 48dp, 72dp, 96dp, 144dp, 192dp, 512dp
   - Format: Vector drawable (XML)

2. **Splash Screen**
   - Logo: 288dp × 288dp
   - Background: solid color (theme-aware)

3. **Sound Effects**
   - Bell sound (timer completion)
   - Multiple options (Chime, Gong, Bell)
   - Format: MP3 or OGG, < 200KB each

4. **Notification Icon**
   - Silhouette style (Android requirement)
   - Size: 24dp × 24dp
   - Format: Vector drawable (XML)
   - Color: White (system tints it)

### If no designer available:
- Use Material Icons for all UI elements
- Use default system notification sound
- Create simple launcher icon with Material Purple + Timer symbol

---

## End of Design Document

This design system provides comprehensive visual specifications for the Yoga Timer Android app. All components follow Material Design 3 guidelines and are optimized for both usability and accessibility. Claude Code should reference this document for all UI implementation decisions.
