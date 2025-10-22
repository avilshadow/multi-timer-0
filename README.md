# Yoga Timer - Android App

A discreet interval timer application for yoga, meditation, and workout routines. Built with Jetpack Compose and Material Design 3.

## Features

- Sequential timer flow with automatic progression
- Text-to-Speech (TTS) for section names and descriptions
- Nested repeat structures (sections within repeats)
- Dual progress indicators (total + current repeat)
- Lock screen display and controls
- Configurable audio feedback (bells, system sounds)
- Background execution with notifications

## Prerequisites

### Windows Setup

1. **Install Java Development Kit (JDK) 17**
   - Download from [Oracle](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or [OpenJDK](https://adoptium.net/)
   - Install and set `JAVA_HOME` environment variable:
     ```
     JAVA_HOME=C:\Program Files\Java\jdk-17
     ```
   - Add to PATH: `%JAVA_HOME%\bin`

2. **Install Android Studio**
   - Download from [Android Studio website](https://developer.android.com/studio)
   - Run the installer (e.g., `android-studio-2023.1.1.28-windows.exe`)
   - Follow the setup wizard to install:
     - Android SDK
     - Android SDK Platform
     - Android Virtual Device (optional for emulator)

3. **Configure SDK Location**
   - Default SDK location: `C:\Users\<YourUsername>\AppData\Local\Android\Sdk`
   - Create a `local.properties` file in the project root (see below)

**For detailed Windows setup instructions, see [WINDOWS_SETUP.md](WINDOWS_SETUP.md)**

### macOS/Linux Setup

1. **Install JDK 17**
   ```bash
   # macOS (using Homebrew)
   brew install openjdk@17

   # Ubuntu/Debian
   sudo apt-get install openjdk-17-jdk
   ```

2. **Install Android Studio**
   - Download from [Android Studio website](https://developer.android.com/studio)
   - Follow platform-specific installation instructions

3. **Configure SDK Location**
   - Default SDK location:
     - macOS: `~/Library/Android/sdk`
     - Linux: `~/Android/Sdk`
   - Create a `local.properties` file in the project root (see below)

## Project Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd multi-timer-0
```

### 2. Create `local.properties`

Create a file named `local.properties` in the project root directory:

**Windows:**
```properties
sdk.dir=C\:\\Users\\<YourUsername>\\AppData\\Local\\Android\\Sdk
```

**macOS:**
```properties
sdk.dir=/Users/<YourUsername>/Library/Android/sdk
```

**Linux:**
```properties
sdk.dir=/home/<YourUsername>/Android/Sdk
```

**Note:** Replace `<YourUsername>` with your actual username. The backslashes in Windows paths must be escaped (`\\`).

### 3. Build the Project

**Using Gradle Wrapper (Recommended):**

```bash
# Windows
gradlew.bat assembleDebug

# macOS/Linux
./gradlew assembleDebug
```

**Using Android Studio:**

1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the project folder
4. Click OK
5. Wait for Gradle sync to complete
6. Click "Run" (▶️) or press Shift+F10

### 4. Install on Device/Emulator

**Using Gradle:**
```bash
# Windows
gradlew.bat installDebug

# macOS/Linux
./gradlew installDebug
```

**Using ADB directly:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Build Variants

- **Debug:** Development build with debugging enabled
- **Release:** Production build with ProGuard/R8 optimization

## Gradle Tasks

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing configuration)
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumentation tests (requires connected device)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Check for dependency updates
./gradlew dependencyUpdates
```

## GitHub Actions CI/CD

This project includes automated builds via GitHub Actions. See [`.github/workflows/android-build.yml`](.github/workflows/android-build.yml).

The CI pipeline:
- ✅ Builds on every push and pull request
- ✅ Runs unit tests
- ✅ Generates debug APK artifact
- ✅ Runs on Ubuntu (Linux) runners

**Note:** No SDK setup needed for GitHub Actions - it's handled automatically!

## Project Structure

```
app/src/main/java/com/yogatimer/app/
├── data/                          # Data layer
│   ├── local/database/            # Room database
│   ├── mapper/                    # Entity-Domain mappers
│   └── repository/                # Repository implementations
├── domain/                        # Domain layer
│   ├── model/                     # Domain models
│   ├── repository/                # Repository interfaces
│   └── usecase/                   # Use cases
├── presentation/                  # UI layer
│   ├── screens/                   # Compose screens
│   ├── components/                # Reusable UI components
│   └── theme/                     # Material Design 3 theme
├── service/                       # Android services
│   ├── timer/                     # Timer foreground service
│   ├── tts/                       # Text-to-Speech
│   ├── audio/                     # Audio feedback
│   └── notification/              # Notification manager
└── util/                          # Utilities
```

## Architecture

This app follows **Clean Architecture** principles with three layers:

1. **Presentation Layer** - Jetpack Compose UI + ViewModels
2. **Domain Layer** - Business logic, use cases, domain models
3. **Data Layer** - Repository implementations, Room database

**Tech Stack:**
- Kotlin
- Jetpack Compose (UI)
- Material Design 3
- Room (Database)
- Hilt (Dependency Injection)
- Coroutines & Flow (Async)
- ViewModel (State Management)

## Troubleshooting

### Issue: "SDK location not found"

**Solution:** Create `local.properties` file with correct SDK path (see above).

### Issue: "Gradle sync failed"

**Solution:**
1. Check internet connection (Gradle downloads dependencies)
2. Invalidate caches: File → Invalidate Caches → Invalidate and Restart
3. Delete `.gradle` folder and sync again

### Issue: "JAVA_HOME not set"

**Solution:**
```bash
# Windows (Command Prompt)
set JAVA_HOME=C:\Program Files\Java\jdk-17

# Windows (PowerShell)
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"

# macOS/Linux
export JAVA_HOME=/path/to/jdk-17
```

### Issue: Build fails with "Out of memory"

**Solution:** Increase Gradle memory in `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

## License

[Add your license here]

## Contributing

[Add contribution guidelines here]

## Contact

[Add contact information here]
