package com.v1media.data

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Transformer
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.ExportException
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class Media3Extractor(private val context: Context) {

    suspend fun extractAudio(inputUri: Uri, outputFile: File) {
        val transformer = Transformer.Builder(context)
            .setAudioMimeType(MimeTypes.AUDIO_AAC) // Modern hardware-accelerated format
            .build()

        val mediaItem = MediaItem.fromUri(inputUri)
        val editedMediaItem = EditedMediaItem.Builder(mediaItem)
            .setRemoveVideo(true) // Crucial for audio extraction
            .build()

        return suspendCancellableCoroutine { continuation ->
            val listener = object : Transformer.Listener {
                override fun onCompleted(composition: androidx.media3.transformer.Composition, exportResult: ExportResult) {
                    continuation.resume(Unit)
                }

                override fun onError(composition: androidx.media3.transformer.Composition, exportResult: ExportResult, exportException: ExportException) {
                    continuation.resumeWithException(exportException)
                }
            }

            transformer.addListener(listener)
            transformer.start(editedMediaItem, outputFile.absolutePath)

            continuation.invokeOnCancellation {
                transformer.cancel()
            }
        }
    }
}
