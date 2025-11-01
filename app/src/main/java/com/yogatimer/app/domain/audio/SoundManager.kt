package com.yogatimer.app.domain.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import com.yogatimer.app.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SoundManager handles audio playback for timer events.
 *
 * Features:
 * - Plays completion sound when timer finishes
 * - Respects user sound settings (enable/disable, volume)
 * - Uses system default notification sound or custom sound
 * - Manages MediaPlayer lifecycle
 */
@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) {

    private var mediaPlayer: MediaPlayer? = null

    /**
     * Play the timer completion sound.
     *
     * Checks settings and plays appropriate sound if enabled.
     */
    suspend fun playTimerCompletionSound() {
        val settings = settingsRepository.getSettingsSync()

        // Check if sound effects are enabled
        if (!settings.enableSoundEffects) {
            return
        }

        try {
            // Release any existing player
            releaseMediaPlayer()

            // Get sound URI
            val soundUri = getSoundUri(settings.completionSoundUri)

            // Create and configure MediaPlayer
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                        .build()
                )

                setDataSource(context, soundUri)
                setVolume(settings.soundVolume, settings.soundVolume)

                setOnCompletionListener {
                    releaseMediaPlayer()
                }

                setOnErrorListener { _, _, _ ->
                    releaseMediaPlayer()
                    true
                }

                prepare()
                start()
            }
        } catch (e: Exception) {
            // Silently fail - don't crash the app if sound doesn't play
            releaseMediaPlayer()
        }
    }

    /**
     * Play a short beep sound (for warnings, etc.).
     */
    suspend fun playBeepSound() {
        val settings = settingsRepository.getSettingsSync()

        if (!settings.enableSoundEffects) {
            return
        }

        try {
            releaseMediaPlayer()

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                        .build()
                )

                setDataSource(context, soundUri)
                setVolume(settings.soundVolume * 0.5f, settings.soundVolume * 0.5f) // Quieter beep

                setOnCompletionListener {
                    releaseMediaPlayer()
                }

                prepare()
                start()
            }
        } catch (e: Exception) {
            releaseMediaPlayer()
        }
    }

    /**
     * Get the appropriate sound URI based on settings.
     */
    private fun getSoundUri(soundUriString: String): Uri {
        return when (soundUriString) {
            "system_default" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            "alarm" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            "ringtone" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            else -> {
                // Try to parse as URI, fallback to default if invalid
                try {
                    Uri.parse(soundUriString)
                } catch (e: Exception) {
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                }
            }
        }
    }

    /**
     * Release MediaPlayer resources.
     */
    private fun releaseMediaPlayer() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }

    /**
     * Clean up resources.
     */
    fun release() {
        releaseMediaPlayer()
    }
}
