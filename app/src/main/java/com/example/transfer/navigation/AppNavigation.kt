package com.example.transfer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.transfer.ui.screens.DetailScreen
import com.example.transfer.ui.screens.HomeScreen
import com.example.transfer.ui.screens.LoginScreen
import com.example.transfer.ui.screens.SeatSelectionScreen
import com.example.transfer.viewmodel.LoginViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val loginViewModel = LoginViewModel()

    NavHost(navController = navController, startDestination = "login") {
        // Pantalla de Login
        composable("login") {
            LoginScreen(viewModel = loginViewModel) {
                navController.navigate("home")
            }
        }

        // Pantalla de Inicio (Home)
        composable("home") {
            HomeScreen(navController = navController)
        }

        // Pantalla de Selección de Asientos
        composable(
            route = "seat_selection/{tripTitle}",
            arguments = listOf(navArgument("tripTitle") { type = NavType.StringType })
        ) { backStackEntry ->
            val tripTitle = backStackEntry.arguments?.getString("tripTitle") ?: ""
            SeatSelectionScreen(navController = navController, tripTitle = tripTitle)
        }

        // Pantalla de Detalles (opcional)
        composable(
            route = "detail/{title}/{imageRes}/{recommendations}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("imageRes") { type = NavType.IntType },
                navArgument("recommendations") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val imageRes = backStackEntry.arguments?.getInt("imageRes") ?: 0
            val recommendations = backStackEntry.arguments?.getString("recommendations") ?: ""
            DetailScreen(title = title, imageRes = imageRes, recommendations = recommendations)
        }
    }
}
