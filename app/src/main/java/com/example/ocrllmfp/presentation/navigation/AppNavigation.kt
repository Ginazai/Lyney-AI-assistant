package com.example.ocrllmfp.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ocrllmfp.presentation.screens.auth.LoginScreen
import com.example.ocrllmfp.presentation.screens.auth.SignUpScreen
import com.example.ocrllmfp.presentation.screens.camera.CameraScreen
import com.example.ocrllmfp.presentation.screens.home.HomeScreen
import com.example.ocrllmfp.presentation.screens.result.ResultScreen
import com.example.ocrllmfp.presentation.screens.settings.SettingsScreen
import com.example.ocrllmfp.presentation.viewmodel.MainViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object Camera : Screen("camera")
    object Result : Screen("result")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation(
    navController: NavHostController,  // ← Usar el que viene como parámetro
    viewModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    val startDestination = if (uiState.isAuthenticated) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,  // ← NO crear uno nuevo
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                viewModel = viewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToCamera = {
                    viewModel.resetProcessing()
                    navController.navigate(Screen.Camera.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                onImageCaptured = { bitmap ->  // ← Pasar lambda, NO navController
                    viewModel.processImage(bitmap)
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.Camera.route) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Result.route) {
            ResultScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack(Screen.Home.route, false)
                },
                onNewPhoto = {
                    viewModel.resetProcessing()
                    navController.navigate(Screen.Camera.route) {
                        popUpTo(Screen.Result.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    viewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}