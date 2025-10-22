# Android SDK Setup Guide for Windows

This guide will walk you through setting up the Android development environment on Windows.

## Step 1: Install Java Development Kit (JDK) 17

### Option A: Oracle JDK (Requires Oracle Account)

1. Visit [Oracle JDK 17 Downloads](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
2. Download `Windows x64 Installer` (e.g., `jdk-17_windows-x64_bin.exe`)
3. Run the installer and follow the prompts
4. Default installation path: `C:\Program Files\Java\jdk-17`

### Option B: Eclipse Temurin (OpenJDK - Recommended)

1. Visit [Adoptium Temurin](https://adoptium.net/temurin/releases/)
2. Select:
   - **Version:** 17 - LTS
   - **Operating System:** Windows
   - **Architecture:** x64
   - **Package Type:** JDK
3. Download the `.msi` installer
4. Run the installer
5. **Important:** Check the box "Set JAVA_HOME variable" during installation

### Verify Java Installation

Open Command Prompt and run:
```cmd
java -version
```

You should see output like:
```
openjdk version "17.0.9" 2023-10-17
```

### Manually Set JAVA_HOME (if needed)

1. Press `Win + R`, type `sysdm.cpl`, press Enter
2. Go to **Advanced** tab → **Environment Variables**
3. Under **System variables**, click **New**:
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Java\jdk-17` (or your JDK path)
4. Find **Path** variable, click **Edit**, add:
   ```
   %JAVA_HOME%\bin
   ```
5. Click OK on all dialogs
6. **Restart** Command Prompt to apply changes

## Step 2: Install Android Studio

### Download

1. Visit [Android Studio Download Page](https://developer.android.com/studio)
2. Click **Download Android Studio**
3. Accept the terms and conditions
4. Download the `.exe` installer (approximately 1 GB)

### Install

1. Run the downloaded `.exe` file (e.g., `android-studio-2023.1.1.28-windows.exe`)
2. Follow the setup wizard:
   - Click **Next**
   - Select components (keep all defaults checked)
   - Choose installation location (default: `C:\Program Files\Android\Android Studio`)
   - Click **Install**
3. When installation completes, click **Finish**

### Initial Setup Wizard

Android Studio will launch the setup wizard:

1. **Import Settings:** Choose "Do not import settings" (first time)
2. **Data Sharing:** Choose your preference
3. **Install Type:** Select **Standard**
4. **Select UI Theme:** Choose Light or Dark
5. **Verify Settings:** Review and click **Finish**
6. Wait for component downloads (this may take 10-30 minutes):
   - Android SDK
   - Android SDK Platform
   - Android SDK Build-Tools
   - Android Emulator
   - Android SDK Platform-Tools

### SDK Location

The default SDK location is:
```
C:\Users\<YourUsername>\AppData\Local\Android\Sdk
```

To verify or change:
1. In Android Studio: **File** → **Settings** (or **Ctrl+Alt+S**)
2. Navigate to **Appearance & Behavior** → **System Settings** → **Android SDK**
3. Note the **Android SDK Location** path

## Step 3: Configure Project

### Create `local.properties`

In your project root folder, create a file named `local.properties`:

```properties
sdk.dir=C\:\\Users\\<YourUsername>\\AppData\\Local\\Android\\Sdk
```

**Important Notes:**
- Replace `<YourUsername>` with your Windows username
- Use double backslashes (`\\`) to escape paths
- Example: `sdk.dir=C\:\\Users\\JohnDoe\\AppData\\Local\\Android\\Sdk`

### Alternative: Find Your SDK Path

If you're not sure of your SDK path:

1. Open Android Studio
2. Create a new blank project
3. Go to **File** → **Settings** → **Appearance & Behavior** → **System Settings** → **Android SDK**
4. Copy the **Android SDK Location** path
5. In the path, replace single backslashes (`\`) with double backslashes (`\\`)

## Step 4: Build the Project

### Using Android Studio (Recommended for Beginners)

1. Open Android Studio
2. Click **File** → **Open**
3. Navigate to the project folder (`multi-timer-0`)
4. Click **OK**
5. Wait for Gradle sync (first sync may take several minutes)
6. Once sync completes, click the **Run** button (▶️) or press `Shift+F10`

### Using Command Line

Open Command Prompt in the project directory:

```cmd
cd C:\path\to\multi-timer-0
gradlew.bat assembleDebug
```

The APK will be generated at:
```
app\build\outputs\apk\debug\app-debug.apk
```

## Step 5: Run on Device/Emulator

### Option A: Physical Android Device

1. Enable **Developer Options** on your Android phone:
   - Go to **Settings** → **About Phone**
   - Tap **Build Number** 7 times
2. Enable **USB Debugging**:
   - Go to **Settings** → **System** → **Developer Options**
   - Enable **USB Debugging**
3. Connect your phone via USB
4. On first connection, approve the USB debugging prompt on your phone
5. In Android Studio, select your device from the device dropdown
6. Click **Run** (▶️)

### Option B: Android Emulator

1. In Android Studio, click **Device Manager** (phone icon on right sidebar)
2. Click **Create Device**
3. Select a device definition (e.g., Pixel 6)
4. Select a system image (e.g., API 34 - Android 14)
   - Click **Download** if not already installed
5. Click **Finish**
6. Start the emulator by clicking the ▶️ icon next to the device
7. Once emulator is running, click **Run** in Android Studio

## Troubleshooting

### Issue: "JAVA_HOME is not set"

**Solution:**
```cmd
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%
```

Or set permanently via System Environment Variables (see Step 1).

### Issue: "SDK location not found"

**Solution:** Create `local.properties` file with correct SDK path (see Step 3).

### Issue: "Gradle sync failed"

**Solutions:**
1. Check internet connection
2. **File** → **Invalidate Caches** → **Invalidate and Restart**
3. Delete `.gradle` folder in project directory and sync again
4. **File** → **Sync Project with Gradle Files**

### Issue: "Unable to find bundled Java version"

**Solution:**
1. Go to **File** → **Settings** → **Build, Execution, Deployment** → **Build Tools** → **Gradle**
2. Under **Gradle JDK**, select your installed JDK 17

### Issue: Slow Gradle Build

**Solution:** Increase Gradle memory in `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
```

### Issue: Emulator Won't Start

**Solutions:**
1. Enable **Virtualization** in BIOS (Intel VT-x or AMD-V)
2. Disable **Hyper-V** on Windows:
   ```cmd
   bcdedit /set hypervisorlaunchtype off
   ```
   Then restart your computer
3. Use a lower API level system image (e.g., API 30 instead of 34)

## Next Steps

Once your environment is set up:

1. ✅ Build the project: `gradlew.bat assembleDebug`
2. ✅ Run tests: `gradlew.bat test`
3. ✅ Install on device: `gradlew.bat installDebug`
4. ✅ Open in Android Studio and explore the code

## Additional Resources

- [Android Developer Documentation](https://developer.android.com/docs)
- [Gradle Build Tool](https://docs.gradle.org/)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
