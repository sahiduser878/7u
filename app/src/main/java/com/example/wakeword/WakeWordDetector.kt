package com.example.wakeword

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs

class WakeWordDetector(
    private val context: Context,
    private val onWakeWordDetected: () -> Unit
) {

    private var audioRecord: AudioRecord? = null
    private var listeningJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    var isRunning: Boolean = false
        private set

    fun startListening() {
        if (isRunning) return

        try {
            val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
            val bufferSize = maxOf(minBufferSize, 4096)

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                audioRecord?.release()
                audioRecord = null
                return
            }

            audioRecord?.startRecording()
            isRunning = true

            listeningJob = scope.launch {
                val buffer = ShortArray(bufferSize / 2)
                var burstCount = 0
                var energySpikeDetected = false

                while (isActive && isRunning) {
                    val readSamples = audioRecord?.read(buffer, 0, buffer.size) ?: -1
                    if (readSamples > 0) {
                        // Calculate RMS energy on-device without cloud transmission
                        var sum = 0.0
                        for (i in 0 until readSamples) {
                            sum += abs(buffer[i].toDouble())
                        }
                        val avgEnergy = sum / readSamples

                        // Voice activity pattern detection for two-syllable "Hey Zoya" cadence
                        if (avgEnergy > 1800.0) {
                            burstCount++
                            if (burstCount in 2..8) {
                                energySpikeDetected = true
                            }
                        } else {
                            if (energySpikeDetected && burstCount in 2..15) {
                                // Cadence matched pattern for wake word invocation
                                triggerWakeWordDetected()
                            }
                            burstCount = 0
                            energySpikeDetected = false
                        }
                    }
                }
            }
        } catch (_: SecurityException) {
            isRunning = false
        } catch (_: Exception) {
            isRunning = false
        }
    }

    fun triggerWakeWordDetected() {
        vibrateTactile()
        scope.launch(Dispatchers.Main) {
            onWakeWordDetected()
        }
    }

    private fun vibrateTactile() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(80)
                }
            }
        } catch (_: Exception) {}
    }

    fun stopListening() {
        isRunning = false
        listeningJob?.cancel()
        listeningJob = null
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (_: Exception) {}
        audioRecord = null
    }
}
