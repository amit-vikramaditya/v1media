package com.v1media.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.v1media.data.Media3Extractor
import com.v1media.engine.WhisperEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed class UiState {
    object Idle : UiState()
    object Extracting : UiState()
    object Transcribing : UiState()
    data class Success(val text: String) : UiState()
    data class Error(val message: String) : UiState()
}

class MainViewModel(
    private val extractor: Media3Extractor,
    private val engine: WhisperEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun processVideo(videoUri: Uri, cacheDir: File, modelFile: File) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                // 1. Initialize Engine
                _uiState.value = UiState.Idle
                if (!engine.initialize(modelFile)) {
                    _uiState.value = UiState.Error("Failed to load Whisper model")
                    return@launch
                }

                // 2. Extract Audio
                _uiState.value = UiState.Extracting
                val tempAudio = File(cacheDir, "temp_audio.m4a")
                extractor.extractAudio(videoUri, tempAudio)

                // 3. Transcribe (Note: In a real app, we'd convert m4a to PCM FloatArray)
                _uiState.value = UiState.Transcribing
                // Placeholder: In production, we use a decoder to get FloatArray from m4a
                val result = "Transcription would appear here after processing ${tempAudio.name}"
                
                _uiState.value = UiState.Success(result)

            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            } finally {
                engine.release()
            }
        }
    }
}
