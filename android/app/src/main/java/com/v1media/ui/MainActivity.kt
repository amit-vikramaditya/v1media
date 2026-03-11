package com.v1media.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.v1media.data.ModelManager
import com.v1media.data.WhisperModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val modelManager = ModelManager(this)
        
        setContent {
            MaterialTheme {
                MainScreen(modelManager)
            }
        }
    }
}

@Composable
fun MainScreen(modelManager: ModelManager) {
    var selectedModel by remember { mutableStateOf<WhisperModel?>(null) }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("v1media Settings", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Choose Local AI Model", style = MaterialTheme.typography.titleMedium)
            Text("Models are downloaded from Hugging Face and stored locally for privacy.", style = MaterialTheme.typography.bodySmall)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn {
                items(modelManager.availableModels) { model ->
                    ModelRow(
                        model = model,
                        isDownloaded = modelManager.isModelDownloaded(model),
                        isSelected = selectedModel == model,
                        onDownload = { modelManager.downloadModel(model) },
                        onSelect = { selectedModel = model }
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { /* File Picker Launcher */ },
                enabled = selectedModel != null && modelManager.isModelDownloaded(selectedModel!!),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select & Transcribe Media")
            }
        }
    }
}

@Composable
fun ModelRow(
    model: WhisperModel,
    isDownloaded: Boolean,
    isSelected: Boolean,
    onDownload: () -> Unit,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
        colors = if (isSelected) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = model.name, style = MaterialTheme.typography.titleMedium)
                Text(text = model.size, style = MaterialTheme.typography.bodySmall)
            }
            
            if (isDownloaded) {
                RadioButton(selected = isSelected, onClick = onSelect)
            } else {
                TextButton(onClick = onDownload) {
                    Text("Download")
                }
            }
        }
    }
}
