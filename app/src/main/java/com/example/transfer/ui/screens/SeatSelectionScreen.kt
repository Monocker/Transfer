package com.example.transfer.ui.screens

import android.content.Context

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // Import necesario
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.transfer.R
import com.example.transfer.viewmodel.ReservationViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    navController: NavController,
    viewModel: ReservationViewModel
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("ReservationPrefs", Context.MODE_PRIVATE)

    // Recuperar la reserva actual desde el ViewModel
    val reservation = viewModel.currentReservation

    if (reservation == null) {
        // Si no hay reserva actual, regresar a HomeScreen
        navController.popBackStack("home", inclusive = false)
        return
    }

    val tripTitle = reservation.model
    val zone = reservation.zone
    val totalSeats = reservation.totalSeats
    val specialSeats = listOf("A1", "A2") // Asientos especiales: conductor y copiloto

    // Cargar estados iniciales de los asientos desde el ViewModel
    val seatAssignments = remember {
        mutableStateMapOf<String, String>().apply {
            putAll(viewModel.getSeatStates(tripTitle, zone, sharedPreferences))
        }
    }

    // Estado derivado para contar los asientos seleccionados
    val selectedSeatsCount by remember {
        derivedStateOf {
            seatAssignments.values.count { it == "Seleccionado" }
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Seleccionar Asientos para $tripTitle") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Información sobre el viaje
            Text(
                text = "Zona: $zone",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Text(
                text = "Asientos totales: $totalSeats",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Indicadores de estado
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                StatusIndicator(label = "Disponible", iconRes = R.drawable.disponible)
                StatusIndicator(label = "Ocupado", iconRes = R.drawable.reservado)
                StatusIndicator(label = "Seleccionado", iconRes = R.drawable.seleccionado)
                StatusIndicator(label = "Conductor", iconRes = R.drawable.conductor)
            }

            // Renderización de filas de asientos
            SeatSelection(
                specialSeats = specialSeats,
                seatAssignments = seatAssignments,
                onSeatConfirm = {
                    val selectedSeats = seatAssignments.filter { it.value == "Seleccionado" }.keys.toList()
                    if (selectedSeats.isEmpty()) {
                        // Mostrar mensaje de error si no se seleccionaron asientos
                        Log.e("SeatSelectionScreen", "No se han seleccionado asientos.")
                        return@SeatSelection
                    }

                    val referenceCode = generateReferenceCode()

                    // Actualizar los estados de los asientos
                    selectedSeats.forEach { seatId ->
                        seatAssignments[seatId] = "Ocupado-$referenceCode"
                    }

                    // Actualiza los estados y sincroniza con SharedPreferences
                    viewModel.updateSeatStates(tripTitle, zone, seatAssignments, sharedPreferences)

                    // Actualizar la reserva con el ID y los asientos seleccionados
                    val updatedReservation = reservation.copy(
                        id = referenceCode,
                        seats = selectedSeats,
                        reservedSeats = reservation.reservedSeats + selectedSeats.size,
                        availableSeats = reservation.totalSeats - (reservation.reservedSeats + selectedSeats.size),
                        seatStates = seatAssignments
                    )

                    // Añadir la reserva confirmada al ViewModel
                    viewModel.addConfirmedReservation(updatedReservation)
                    // Establecer la reserva actual como la actualizada
                    viewModel.currentReservation = updatedReservation

                    // Navegar a la pantalla de resumen de reserva
                    navController.navigate("reservation_summary")
                },
                reservedSeats = selectedSeatsCount,
                totalSeats = totalSeats,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun SeatSelection(
    specialSeats: List<String>,
    seatAssignments: MutableMap<String, String>,
    onSeatConfirm: () -> Unit,
    reservedSeats: Int,
    totalSeats: Int,
    modifier: Modifier = Modifier
) {
    val vanRows = listOf(
        listOf("A1", "A2"), // Conductor y copiloto
        listOf("B1", "B2", "", "B3", "B4"), // Pasillo central
        listOf("C1", "C2", "", "C3", "C4"),
        listOf("D1", "D2", "", "D3", "D4"),
        listOf("E1", "E2", "", "E3", "E4")
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        vanRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                row.forEach { seatId ->
                    if (seatId.isEmpty()) {
                        Spacer(modifier = Modifier.width(32.dp)) // Espaciador para el pasillo
                    } else {
                        val seatState = seatAssignments[seatId]?.split("-")?.first() ?: "Disponible"
                        val referenceCode = seatAssignments[seatId]?.split("-")?.getOrNull(1)
                        SeatWithImage(
                            seatId = seatId,
                            seatState = seatState,
                            referenceCode = referenceCode,
                            isSpecial = seatId in specialSeats,
                            onSeatSelected = {
                                if (seatState == "Disponible") {
                                    seatAssignments[seatId] = "Seleccionado"
                                } else if (seatState == "Seleccionado") {
                                    seatAssignments[seatId] = "Disponible"
                                }
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Seleccionados: $reservedSeats",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )

        Button(
            onClick = { onSeatConfirm() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar selección")
        }
    }
}

@Composable
fun SeatWithImage(
    seatId: String,
    seatState: String,
    referenceCode: String?,
    isSpecial: Boolean = false,
    onSeatSelected: () -> Unit
) {
    val iconRes = when {
        isSpecial -> R.drawable.conductor
        seatState == "Disponible" -> R.drawable.disponible
        seatState.startsWith("Ocupado") -> R.drawable.reservado
        seatState == "Seleccionado" -> R.drawable.seleccionado
        else -> R.drawable.disponible
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(70.dp)
            .clickable(
                enabled = !isSpecial && (seatState == "Disponible" || seatState == "Seleccionado")
            ) {
                onSeatSelected()
            }
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )
        Text(
            text = seatId,
            fontSize = 14.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        if (!referenceCode.isNullOrEmpty()) {
            Text(
                text = "Ref: $referenceCode",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatusIndicator(label: String, iconRes: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// Función auxiliar para generar código de referencia
fun generateReferenceCode(): String {
    return Random.nextInt(1000, 9999).toString()
}
