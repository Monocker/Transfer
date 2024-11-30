package com.example.transfer.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.transfer.viewmodel.ReservationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationSummaryScreen(
    navController: NavController,
    viewModel: ReservationViewModel
) {
    // Obtener el contexto
    val context = LocalContext.current

    // Obtenemos la reserva actual desde el ViewModel
    val reservation = viewModel.currentReservation

    if (reservation == null) {
        // Si no hay reserva actual, regresar a HomeScreen
        navController.popBackStack("home", inclusive = false)
        return
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Resumen de Reserva") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("ID de Reserva: ${reservation.id}", fontWeight = FontWeight.Bold)
            Text("Zona: ${reservation.zone}")
            Text("Agencia: ${reservation.agency}")
            Text("Hotel: ${reservation.hotel}")
            Text("Tienda: ${reservation.store}")
            Text("Pick-Up: ${reservation.pickUpTime}")
            Text("Fecha: ${reservation.date}")
            Text("Modelo: ${reservation.model}")
            Text("Pax: ${reservation.pax}")
            Text("Adultos: ${reservation.adults}")
            Text("Menores: ${reservation.children}")
            Text("Cliente: ${reservation.clientName}")
            Text("Asientos: ${reservation.seats.joinToString(", ")}")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Agregar la reserva confirmada
                    viewModel.addConfirmedReservation(reservation)

                    // Actualizar los estados de los asientos y las reservas en el ViewModel
                    val sharedPreferences = context.getSharedPreferences("ReservationPrefs", Context.MODE_PRIVATE)
                    viewModel.updateSeatStates(
                        model = reservation.model,
                        zone = reservation.zone,
                        seatAssignments = reservation.seatStates,
                        sharedPreferences = sharedPreferences
                    )

                    // Limpiar la reserva actual y marcar que la reserva ha sido finalizada
                    viewModel.currentReservation = null
                    viewModel.finalizeReservation()

                    // Regresar a HomeScreen
                    navController.popBackStack("home", inclusive = false)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Finalizar")
            }
        }
    }
}
