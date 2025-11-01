package com.yogatimer.app.domain.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import com.yogatimer.app.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * TTSManager handles Text-to-Speech announcements for timer events.
 *
 * Features:
 * - Announces timer names when starting
 * - Announces section names with repeat information
 * - Respects user TTS settings (enable/disable, language)
 * - Manages TextToSpeech lifecycle
 */
@Singleton
class TTSManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    /**
     * Initialize TTS engine.
     */
    suspend fun initialize(): Boolean = suspendCancellableCoroutine { continuation ->
        tts = TextToSpeech(context) { status ->
            isInitialized = status == TextToSpeech.SUCCESS
            if (isInitialized) {
                // Set default configuration
                tts?.apply {
                    setLanguage(Locale.getDefault())
                    setSpeechRate(0.9f)
                    setPitch(1.0f)
                }
            }
            continuation.resume(isInitialized)
        }
    }

    /**
     * Configure TTS settings (language, speech rate, etc.).
     */
    private suspend fun configureTTS() {
        tts?.apply {
            val settings = settingsRepository.getSettingsSync()

            // Set language
            val locale = parseLocale(settings.ttsLanguage)
            val result = setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to default locale
                setLanguage(Locale.getDefault())
            }

            // Set speech rate (slightly slower for clarity)
            setSpeechRate(0.9f)

            // Set pitch (normal)
            setPitch(1.0f)
        }
    }

    /**
     * Announce a timer name.
     */
    suspend fun announceTimer(timerName: String) {
        val settings = settingsRepository.getSettingsSync()

        if (!settings.enableTTS) {
            return
        }

        if (!isInitialized) {
            initialize()
        }

        // Configure TTS with latest settings before speaking
        configureTTS()

        tts?.speak(
            timerName,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "timer_$timerName"
        )
    }

    /**
     * Announce a section with repeat information.
     */
    suspend fun announceSection(
        sectionName: String,
        currentRepeat: Int,
        totalRepeats: Int
    ) {
        val settings = settingsRepository.getSettingsSync()

        if (!settings.enableTTS) {
            return
        }

        if (!isInitialized) {
            initialize()
        }

        // Configure TTS with latest settings before speaking
        configureTTS()

        val announcement = if (totalRepeats > 1) {
            "$sectionName, repeat $currentRepeat of $totalRepeats"
        } else {
            sectionName
        }

        tts?.speak(
            announcement,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "section_$sectionName"
        )
    }

    /**
     * Announce workout completion.
     */
    suspend fun announceWorkoutComplete() {
        val settings = settingsRepository.getSettingsSync()

        if (!settings.enableTTS) {
            return
        }

        if (!isInitialized) {
            initialize()
        }

        // Configure TTS with latest settings before speaking
        configureTTS()

        tts?.speak(
            "Workout complete! Great job!",
            TextToSpeech.QUEUE_FLUSH,
            null,
            "workout_complete"
        )
    }

    /**
     * Stop any current speech.
     */
    fun stop() {
        tts?.stop()
    }

    /**
     * Release TTS resources.
     */
    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }

    /**
     * Parse locale string (e.g., "en-US") to Locale object.
     */
    private fun parseLocale(localeString: String): Locale {
        val parts = localeString.split("-")
        return when (parts.size) {
            1 -> Locale(parts[0])
            2 -> Locale(parts[0], parts[1])
            else -> Locale.getDefault()
        }
    }
}
