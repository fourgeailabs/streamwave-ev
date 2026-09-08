package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

sealed class VoiceUiState {
    object Idle : VoiceUiState()
    object Listening : VoiceUiState()
    data class Processing(val rawSpeech: String) : VoiceUiState()
    data class Success(val commandType: VoiceCommandType, val rawSpeech: String, val message: String) : VoiceUiState()
    data class Error(val message: String) : VoiceUiState()
}

sealed class VoiceCommandType {
    object Play : VoiceCommandType()
    object Pause : VoiceCommandType()
    object Next : VoiceCommandType()
    object Previous : VoiceCommandType()
    object Shuffle : VoiceCommandType()
    object Repeat : VoiceCommandType()
    object ToggleDriverMode : VoiceCommandType()
    object ShowLyrics : VoiceCommandType()
    object ToggleFavorite : VoiceCommandType()
    data class SearchAndPlay(val query: String) : VoiceCommandType()
    object Unknown : VoiceCommandType()
}

class VoiceCommandManager(
    private val context: Context,
    private val onCommandRecognized: (VoiceCommandType) -> Unit
) {
    private val _uiState = MutableStateFlow<VoiceUiState>(VoiceUiState.Idle)
    val uiState: StateFlow<VoiceUiState> = _uiState.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null

    companion object {
        private const val TAG = "VoiceCommandManager"
    }

    init {
        initializeRecognizer()
    }

    private fun initializeRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _uiState.value = VoiceUiState.Listening
                    }

                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        _uiState.value = VoiceUiState.Processing("Analyzing audio...")
                    }

                    override fun onError(error: Int) {
                        val message = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                            SpeechRecognizer.ERROR_CLIENT -> "Client error"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Audio permission required"
                            SpeechRecognizer.ERROR_NETWORK -> "Network required for voice recognition"
                            SpeechRecognizer.ERROR_NO_MATCH -> "No command heard. Try saying 'Play' or 'Next'"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Voice timeout"
                            else -> "Voice recognition error ($error)"
                        }
                        _uiState.value = VoiceUiState.Error(message)
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val spokenText = matches?.firstOrNull() ?: ""
                        processVoiceInput(spokenText)
                    }

                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        }
    }

    fun startListening() {
        if (speechRecognizer == null) {
            initializeRecognizer()
        }
        _uiState.value = VoiceUiState.Listening

        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "StreamWave EV Voice: Speak a music command")
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start listening: ${e.message}")
            _uiState.value = VoiceUiState.Error("Voice engine initialization failed")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.w(TAG, "Stop error: ${e.message}")
        }
    }

    fun resetState() {
        _uiState.value = VoiceUiState.Idle
    }

    fun executeQuickCommand(commandText: String) {
        processVoiceInput(commandText)
    }

    private fun processVoiceInput(text: String) {
        val lower = text.lowercase().trim()
        val command = when {
            lower.contains("play") && (lower.length > 5 && !lower.endsWith("music") && !lower.endsWith("song")) -> {
                val query = lower.replace("play", "").replace("song", "").replace("track", "").trim()
                VoiceCommandType.SearchAndPlay(query)
            }
            lower == "play" || lower.contains("resume") || lower.contains("start music") -> VoiceCommandType.Play
            lower == "pause" || lower.contains("stop") || lower.contains("freeze") || lower.contains("hold on") -> VoiceCommandType.Pause
            lower.contains("next") || lower.contains("skip") || lower.contains("next song") -> VoiceCommandType.Next
            lower.contains("previous") || lower.contains("back") || lower.contains("replay") || lower.contains("last song") -> VoiceCommandType.Previous
            lower.contains("shuffle") || lower.contains("mix") -> VoiceCommandType.Shuffle
            lower.contains("repeat") || lower.contains("loop") -> VoiceCommandType.Repeat
            lower.contains("driver") || lower.contains("driving") || lower.contains("hud") || lower.contains("car mode") -> VoiceCommandType.ToggleDriverMode
            lower.contains("lyrics") || lower.contains("words") || lower.contains("karaoke") || lower.contains("sing along") -> VoiceCommandType.ShowLyrics
            lower.contains("favorite") || lower.contains("like") || lower.contains("love this") -> VoiceCommandType.ToggleFavorite
            else -> VoiceCommandType.Unknown
        }

        val message = when (command) {
            is VoiceCommandType.Play -> "Resuming music playback"
            is VoiceCommandType.Pause -> "Pausing music"
            is VoiceCommandType.Next -> "Skipping to next track"
            is VoiceCommandType.Previous -> "Returning to previous track"
            is VoiceCommandType.Shuffle -> "Toggling shuffle playback"
            is VoiceCommandType.Repeat -> "Toggling repeat mode"
            is VoiceCommandType.ToggleDriverMode -> "Switching Driver HUD Mode"
            is VoiceCommandType.ShowLyrics -> "Opening Synchronized Lyrics"
            is VoiceCommandType.ToggleFavorite -> "Saved to your driving favorites"
            is VoiceCommandType.SearchAndPlay -> "Searching for '${command.query}'"
            is VoiceCommandType.Unknown -> "Heard: \"$text\". Try 'Play', 'Next', 'Lyrics', or 'Driver Mode'"
        }

        _uiState.value = VoiceUiState.Success(command, text, message)
        if (command !is VoiceCommandType.Unknown) {
            onCommandRecognized(command)
        }
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.w(TAG, "Destroy error: ${e.message}")
        }
        speechRecognizer = null
    }
}
