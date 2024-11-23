package com.example.transfer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.transfer.ui.screens.HomeScreen
import com.example.transfer.ui.screens.LoginScreen
import com.example.transfer.viewmodel.LoginViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val loginViewModel = LoginViewModel()

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
