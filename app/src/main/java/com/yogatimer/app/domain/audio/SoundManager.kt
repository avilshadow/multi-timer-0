package com.yogatimer.app.domain.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import com.yogatimer.app.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SoundManager handles audio playback for timer events.
 *
 * Features:
 * - Plays completion sound when timer finishes
 * - Respects user sound settings (enable/disable, volume)
 * - Uses system default notification sound or custom sound
 * - Manages Ringtone lifecycle
 */
@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) {

    private var ringtone: Ringtone? = null

    /**
     * Play the timer completion sound.
     *
     * Checks settings and plays appropriate sound if enabled.
     */
    suspend fun playTimerCompletionSound() {
        try {
            val settings = settingsRepository.getSettingsSync()

            // Check if sound effects are enabled
            if (!settings.enableSoundEffects) {
                Log.d(TAG, "Sound effects disabled in settings")
                return
            }

            // Stop any currently playing sound
            stopCurrentSound()

            // Get sound URI
            val soundUri = getSoundUri(settings.completionSoundUri)
            Log.d(TAG, "Playing completion sound: ${settings.completionSoundUri}, URI: $soundUri")

            // Create and play ringtone
            ringtone = RingtoneManager.getRingtone(context, soundUri)

            if (ringtone != null) {
                ringtone?.apply {
                    val attrs = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()

                    setAudioAttributes(attrs)
                    play()
                }
                Log.d(TAG, "Sound playing successfully")
            } else {
                Log.e(TAG, "Failed to create Ringtone from URI: $soundUri")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing completion sound", e)
        }
    }

    /**
     * Play a short beep sound (for warnings, etc.).
     */
    suspend fun playBeepSound() {
        try {
            val settings = settingsRepository.getSettingsSync()

            if (!settings.enableSoundEffects) {
                return
            }

            stopCurrentSound()

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            ringtone = RingtoneManager.getRingtone(context, soundUri)
            ringtone?.apply {
                val attrs = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()

                setAudioAttributes(attrs)
                play()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing beep sound", e)
        }
    }

    /**
     * Get the appropriate sound URI based on settings.
     */
    private fun getSoundUri(soundUriString: String): Uri {
        val uri = when (soundUriString) {
            "system_default", "notification" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            "alarm" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            "ringtone" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            "ding" -> {
                // Custom ding sound from raw resources
                Uri.parse("android.resource://${context.packageName}/raw/ding")
            }
            else -> {
                // Try to parse as URI, fallback to default if invalid
                try {
                    Uri.parse(soundUriString)
                } catch (e: Exception) {
                    Log.w(TAG, "Invalid URI string: $soundUriString, using default", e)
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                }
            }
        }

        Log.d(TAG, "Sound URI for '$soundUriString': $uri")
        return uri
    }

    /**
     * Stop any currently playing sound.
     */
    private fun stopCurrentSound() {
        ringtone?.apply {
            if (isPlaying) {
                stop()
            }
        }
        ringtone = null
    }

    /**
     * Stop any current sound.
     */
    fun stop() {
        stopCurrentSound()
    }

    /**
     * Clean up resources.
     */
    fun release() {
        stopCurrentSound()
    }

    companion object {
        private const val TAG = "SoundManager"
    }
}
