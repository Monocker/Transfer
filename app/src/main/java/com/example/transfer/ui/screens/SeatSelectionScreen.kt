package com.example.transfer.ui.screens

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    tripTitle: String,
    zone: String,
    totalSeats: Int,
    context: Context,
    viewModel: ReservationViewModel
) {
    val sharedPreferences = context.getSharedPreferences("seat_prefs", Context.MODE_PRIVATE)
    val specialSeats = listOf("A1", "A2") // Asientos especiales: conductor y copiloto

    // Cargar estados iniciales de los asientos desde el ViewModel o SharedPreferences
    var seatAssignments by remember {
        mutableStateOf(viewModel.getSeatStates(tripTitle, sharedPreferences) ?: loadSeatStates(sharedPreferences))
    }

    // Estado local para contar los asientos seleccionados
    var reservedSeats by remember { mutableStateOf(seatAssignments.count { it.value.startsWith("Seleccionado") }) }

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
                    val referenceCode = generateReferenceCode()
                    seatAssignments.forEach { (seatId, state) ->
                        if (state.startsWith("Seleccionado")) {
                            seatAssignments[seatId] = "Ocupado-$referenceCode"
                        }
                    }
                    // Actualiza los estados y sincroniza con SharedPreferences
                    viewModel.updateSeatStates(tripTitle, seatAssignments, sharedPreferences)
                    navController.popBackStack() // Volver a la pantalla anterior
                },
                onSeatStateChange = { seatId, newState ->
                    seatAssignments[seatId] = newState
                    reservedSeats = seatAssignments.values.count { it.startsWith("Seleccionado") }
                    viewModel.updateSeatStates(tripTitle, seatAssignments, sharedPreferences)
                },
                reservedSeats = reservedSeats,
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
    onSeatStateChange: (String, String) -> Unit,
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
                        val seatState = seatAssignments[seatId] ?: "Disponible"
                        val referenceCode = seatState.split("-").getOrNull(1)
                        SeatWithImage(
                            seatId = seatId,
                            seatState = seatState.split("-")[0],
                            referenceCode = referenceCode,
                            isSpecial = seatId in specialSeats,
                            onSeatSelected = {
                                val currentState = seatAssignments[seatId]?.split("-")?.get(0)
                                if (currentState == "Disponible") {
                                    seatAssignments[seatId] = "Seleccionado"
                                } else if (currentState == "Seleccionado") {
                                    seatAssignments[seatId] = "Disponible"
                                }
                                onSeatStateChange(seatId, seatAssignments[seatId] ?: "Disponible")
                            }
                        )

                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Seleccionados: $reservedSeats/$totalSeats",
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
        seatState == "Ocupado" -> R.drawable.reservado
        seatState == "Seleccionado" -> R.drawable.seleccionado
        else -> R.drawable.disponible
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(70.dp)
            .clickable(enabled = !isSpecial && (seatState == "Disponible" || seatState == "Seleccionado")) {
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

// Funciones auxiliares para guardar y cargar estados
fun saveSeatStates(sharedPreferences: SharedPreferences, seatAssignments: MutableMap<String, String>) {
    sharedPreferences.edit().apply {
        seatAssignments.forEach { (seatId, state) ->
            putString(seatId, state)
        }
        apply()
    }
}

fun loadSeatStates(sharedPreferences: SharedPreferences): MutableMap<String, String> {
    val seatStates = mutableMapOf<String, String>()
    for (row in 'A'..'E') {
        for (col in 1..4) {
            val seatId = "$row$col"
            seatStates[seatId] = sharedPreferences.getString(seatId, "Disponible") ?: "Disponible"
        }
    }
    return seatStates
}

fun generateReferenceCode(): String {
    return Random.nextInt(1000, 9999).toString()
}
