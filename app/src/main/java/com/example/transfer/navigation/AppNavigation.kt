package com.example.transfer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.transfer.ui.screens.HomeScreen
import com.example.transfer.ui.screens.LoginScreen
import com.example.transfer.viewmodel.LoginViewModel
import com.example.transfer.domain.usecase.LoginUseCase
import com.example.transfer.data.repository.UserRepository

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Inicializar dependencias
    val userRepository = UserRepository() // Repositorio que implementa IUserRepository
    val loginUseCase = LoginUseCase(userRepository) // Caso de uso con el repositorio
    val loginViewModel = LoginViewModel(loginUseCase) // ViewModel con el caso de uso

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(viewModel = loginViewModel) {
                navController.navigate("home")
            }
        }
        composable("home") {
            HomeScreen()
        }
    }
}
