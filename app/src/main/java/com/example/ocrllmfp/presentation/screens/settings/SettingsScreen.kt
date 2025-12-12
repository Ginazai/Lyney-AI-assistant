package com.example.ocrllmfp.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ocrllmfp.presentation.viewmodel.MainViewModel
import com.example.ocrllmfp.ui.components.*
import com.example.ocrllmfp.ui.theme.AppTheme

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showThemeSelector by remember { mutableStateOf(false) }

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
                    text = "Configuración",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Selector de Tema
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 16.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showThemeSelector = !showThemeSelector }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Palette,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    text = "Tema",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = getThemeName(uiState.currentTheme),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                        Icon(
                            if (showThemeSelector) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }

                    // Opciones de tema expandibles
                    if (showThemeSelector) {
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeOption(
                                theme = AppTheme.LIGHT,
                                isSelected = uiState.currentTheme == AppTheme.LIGHT,
                                onClick = { viewModel.changeTheme(AppTheme.LIGHT) }
                            )
                            ThemeOption(
                                theme = AppTheme.DARK,
                                isSelected = uiState.currentTheme == AppTheme.DARK,
                                onClick = { viewModel.changeTheme(AppTheme.DARK) }
                            )
                            ThemeOption(
                                theme = AppTheme.BLUE,
                                isSelected = uiState.currentTheme == AppTheme.BLUE,
                                onClick = { viewModel.changeTheme(AppTheme.BLUE) }
                            )
                            ThemeOption(
                                theme = AppTheme.GREEN,
                                isSelected = uiState.currentTheme == AppTheme.GREEN,
                                onClick = { viewModel.changeTheme(AppTheme.GREEN) }
                            )
                            ThemeOption(
                                theme = AppTheme.PURPLE,
                                isSelected = uiState.currentTheme == AppTheme.PURPLE,
                                onClick = { viewModel.changeTheme(AppTheme.PURPLE) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón logout
            GlassButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                text = "Cerrar Sesión",
                backgroundColor = MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
            )
        }
    }

    // Diálogo de confirmación
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ThemeOption(
    theme: AppTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(
                if (isSelected)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else
                    Color.Transparent
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo de color del tema
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(getThemeColor(theme))
                    .border(
                        width = 2.dp,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            Color.Transparent,
                        shape = CircleShape
                    )
            )

            Text(
                text = getThemeName(theme),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

fun getThemeName(theme: AppTheme): String {
    return when (theme) {
        AppTheme.LIGHT -> "Claro"
        AppTheme.DARK -> "Oscuro"
        AppTheme.BLUE -> "Azul Océano"
        AppTheme.GREEN -> "Verde Bosque"
        AppTheme.PURPLE -> "Morado Neón"
    }
}

fun getThemeColor(theme: AppTheme): Color {
    return when (theme) {
        AppTheme.LIGHT -> Color(0xFF6200EE)
        AppTheme.DARK -> Color(0xFF1F1F1F)
        AppTheme.BLUE -> Color(0xFF00197B)
        AppTheme.GREEN -> Color(0xFF40916C)
        AppTheme.PURPLE -> Color(0xFF9D4EDD)
    }
}