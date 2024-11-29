package com.example.transfer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
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
import com.example.transfer.viewmodel.ReservationViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val loginViewModel = LoginViewModel()
    val reservationViewModel = ReservationViewModel() // ViewModel compartido para las pantallas

    NavHost(navController = navController, startDestination = "login") {
        // Pantalla de Login
        composable("login") {
            LoginScreen(viewModel = loginViewModel) {
                navController.navigate("home")
            }
        }

        // Pantalla de Inicio (Home)
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = reservationViewModel // Pasar el ViewModel
            )
        }

        // Pantalla de Selección de Asientos
        composable(
            route = "seat_selection/{tripTitle}/{zone}/{totalSeats}",
            arguments = listOf(
                navArgument("tripTitle") { type = NavType.StringType },
                navArgument("zone") { type = NavType.StringType },
                navArgument("totalSeats") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val tripTitle = backStackEntry.arguments?.getString("tripTitle") ?: ""
            val zone = backStackEntry.arguments?.getString("zone") ?: ""
            val totalSeats = backStackEntry.arguments?.getInt("totalSeats") ?: 0
            val context = LocalContext.current // Obtener el contexto aquí

            SeatSelectionScreen(
                navController = navController,
                tripTitle = tripTitle,
                zone = zone,
                totalSeats = totalSeats,
                context = context, // Pasar el contexto
                viewModel = reservationViewModel // Pasar el ViewModel
            )
        }


        // Pantalla de Detalles
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
