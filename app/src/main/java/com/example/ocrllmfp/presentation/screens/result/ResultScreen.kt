package com.example.ocrllmfp.presentation.screens.result

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.unit.dp
import com.example.ocrllmfp.presentation.viewmodel.MainViewModel
import com.example.ocrllmfp.presentation.viewmodel.ProcessingStage
import com.example.ocrllmfp.ui.components.*
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun ResultScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNewPhoto: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "Resultados",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contenido
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Mostrar loader si está procesando
                if (uiState.isProcessing) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 16.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 4.dp
                            )
                            Text(
                                text = when (uiState.processingStage) {
                                    ProcessingStage.OCR -> "Extrayendo texto..."
                                    ProcessingStage.ANALYZING -> "Analizando contenido..."
                                    ProcessingStage.AI_PROCESSING -> "Procesando con IA..."
                                    else -> "Procesando..."
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Mostrar error si existe
                if (uiState.error != null) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 16.dp
                    ) {
                        Column {
                            Text(
                                text = "⚠️ Error",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.error ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Texto extraído (colapsable)
                if (uiState.extractedText != null && !uiState.isProcessing) {
                    var showExtracted by remember { mutableStateOf(false) }

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 16.dp
                    ) {
                        Column {
                            TextButton(
                                onClick = { showExtracted = !showExtracted },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Texto Extraído",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        if (showExtracted) "▼" else "▶",
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            if (showExtracted) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                Text(
                                    text = uiState.extractedText ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                // Respuesta IA con Markdown
                if (uiState.aiResponse != null && !uiState.isProcessing) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp
                    ) {
                        Column {
                            Text(
                                text = "Análisis IA",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Renderizar Markdown
                            MarkdownText(
                                markdown = uiState.aiResponse ?: "",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón nueva foto
                GlassButton(
                    onClick = {
                        viewModel.resetProcessing()
                        onNewPhoto()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    text = "Nueva Foto",
                    backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}