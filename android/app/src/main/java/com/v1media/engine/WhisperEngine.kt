package com.v1media.engine

import android.content.Context
import java.io.File

class WhisperEngine(private val context: Context) {
    private var nativeContextPtr: Long = 0

    companion object {
        init {
            System.loadLibrary("whisper_jni")
        }
    }

    external fun initContext(modelPath: String): Long
    external fun freeContext(ctxPtr: Long)
    external fun transcribe(ctxPtr: Long, audioData: FloatArray): String

    fun initialize(modelFile: File): Boolean {
        if (!modelFile.exists()) return false
        nativeContextPtr = initContext(modelFile.absolutePath)
        return nativeContextPtr != 0L
    }

    fun processAudio(audioData: FloatArray): String {
        if (nativeContextPtr == 0L) return "Error: Engine not initialized"
        return transcribe(nativeContextPtr, audioData)
    }

    fun release() {
        if (nativeContextPtr != 0L) {
            freeContext(nativeContextPtr)
            nativeContextPtr = 0L
        }
    }
}
