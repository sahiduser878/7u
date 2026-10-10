package com.example.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class AudioVoiceEngine(
    private val context: Context,
    private val onTextRecognized: (String) -> Unit,
    private val onErrorOccurred: (String) -> Unit,
    private val onPartialRecognized: (String) -> Unit = {}
) : RecognitionListener, TextToSpeech.OnInitListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    private val _micAmplitude = MutableStateFlow(0.1f)
    val micAmplitude: StateFlow<Float> = _micAmplitude.asStateFlow()

    private val _isSpeakingState = MutableStateFlow(false)
    val isSpeakingState: StateFlow<Boolean> = _isSpeakingState.asStateFlow()

    private var onSpeechDoneCallback: (() -> Unit)? = null

    init {
        initSpeechRecognizer()
        tts = TextToSpeech(context.applicationContext, this)
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(this@AudioVoiceEngine)
            }
        }
    }

    fun startListening(selectedLang: String = "hi-IN") {
        if (speechRecognizer == null) {
            initSpeechRecognizer()
        }

        stopSpeaking()

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)

            // Multilingual Hindi, Bengali, English preference
            when {
                selectedLang.contains("Hindi", ignoreCase = true) -> {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                }
                selectedLang.contains("Bengali", ignoreCase = true) -> {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-IN")
                }
                selectedLang.contains("English", ignoreCase = true) -> {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
                }
                else -> {
                    // Auto-detect mixed Hinglish / multilingual
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag())
                    putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("hi-IN", "bn-IN", "en-IN"))
                }
            }
        }

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            onErrorOccurred("Could not start microphone: ${e.localizedMessage}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            _micAmplitude.value = 0.05f
        } catch (_: Exception) {}
    }

    fun cancelListening() {
        try {
            speechRecognizer?.cancel()
            _micAmplitude.value = 0.05f
        } catch (_: Exception) {}
    }

    fun speak(text: String, onDone: (() -> Unit)? = null) {
        onSpeechDoneCallback = onDone
        if (!isTtsInitialized || tts == null) {
            onDone?.invoke()
            return
        }

        stopSpeaking()
        _isSpeakingState.value = true

        // Detect if text contains Hindi or Bengali characters for TTS locale
        val containsDevanagari = text.any { it in '\u0900'..'\u097F' }
        val containsBengali = text.any { it in '\u0980'..'\u09FF' }

        if (containsDevanagari) {
            tts?.language = Locale.forLanguageTag("hi-IN")
        } else if (containsBengali) {
            tts?.language = Locale.forLanguageTag("bn-IN")
        } else {
            tts?.language = Locale.ENGLISH
        }

        val utteranceId = "ZOYA_TTS_${System.currentTimeMillis()}"
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stopSpeaking() {
        try {
            tts?.stop()
            _isSpeakingState.value = false
        } catch (_: Exception) {}
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            tts?.setPitch(1.08f) // Friendly natural female pitch
            tts?.setSpeechRate(1.02f)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeakingState.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeakingState.value = false
                    onSpeechDoneCallback?.invoke()
                }

                override fun onError(utteranceId: String?) {
                    _isSpeakingState.value = false
                    onSpeechDoneCallback?.invoke()
                }
            })
        }
    }

    // SpeechRecognizer Callbacks
    override fun onReadyForSpeech(params: Bundle?) {
        _micAmplitude.value = 0.2f
    }

    override fun onBeginningOfSpeech() {
        _micAmplitude.value = 0.6f
    }

    override fun onRmsChanged(rmsdB: Float) {
        // Normalize rmsdB (-2 to 10 dB typically) to 0.1 .. 1.0 range
        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.1f, 1.0f)
        _micAmplitude.value = normalized
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        _micAmplitude.value = 0.1f
    }

    override fun onError(error: Int) {
        _micAmplitude.value = 0.05f
        val msg = when (error) {
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please try again."
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
            SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout during recognition."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected in time."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required."
            else -> "Recognition issue (code $error)."
        }
        onErrorOccurred(msg)
    }

    override fun onResults(results: Bundle?) {
        _micAmplitude.value = 0.05f
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val bestResult = matches?.firstOrNull()
        if (!bestResult.isNullOrBlank()) {
            onTextRecognized(bestResult)
        } else {
            onErrorOccurred("Could not understand clearly.")
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val best = matches?.firstOrNull()
        if (!best.isNullOrBlank()) {
            onPartialRecognized(best)
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}

    fun destroy() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
            tts?.stop()
            tts?.shutdown()
            tts = null
        } catch (_: Exception) {}
    }
}
