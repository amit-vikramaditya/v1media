package com.v1media.data

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File

data class WhisperModel(
    val name: String,
    val size: String,
    val url: String,
    val filename: String
)

class ModelManager(private val context: Context) {
    
    val availableModels = listOf(
        WhisperModel("Tiny", "75 MB", "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-tiny.bin", "ggml-tiny.bin"),
        WhisperModel("Base", "147 MB", "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-base.bin", "ggml-base.bin"),
        WhisperModel("Small", "465 MB", "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-small.bin", "ggml-small.bin")
    )

    fun isModelDownloaded(model: WhisperModel): Boolean {
        val file = File(context.filesDir, "models/${model.filename}")
        return file.exists()
    }

    fun downloadModel(model: WhisperModel) {
        val request = DownloadManager.Request(Uri.parse(model.url))
            .setTitle("Downloading Whisper ${model.name} Model")
            .setDescription("Required for local transcription")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, model.filename)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    fun getModelFile(model: WhisperModel): File {
        return File(context.filesDir, "models/${model.filename}")
    }
}
