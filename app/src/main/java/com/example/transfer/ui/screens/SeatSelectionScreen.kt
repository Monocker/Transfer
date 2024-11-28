package com.example.transfer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.transfer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(navController: NavController, tripTitle: String) {
    val specialSeats = listOf("A1", "A2") // Asientos del conductor y copiloto
    var seatAssignments by remember { mutableStateOf(mutableMapOf<String, String>()) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Select Seats for $tripTitle") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        SeatSelection(
            specialSeats = specialSeats,
            seatAssignments = seatAssignments,
            onSeatConfirm = { navController.popBackStack() },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun SeatSelection(
    specialSeats: List<String>,
    seatAssignments: MutableMap<String, String>,
    onSeatConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Filas predefinidas con formato de van
    val vanRows = listOf(
        listOf("A1", "A2"), // Conductor y copiloto
        listOf("B1", "B2", "", "B3", "B4"), // Pasillo en el medio
        listOf("C1", "C2", "", "C3", "C4"),
        listOf("D1", "D2", "", "D3", "D4"),
        listOf("E1", "E2", "", "E3", "E4")
    )

    // Estado reactivo para asientos seleccionados
    var currentSeatAssignments by remember { mutableStateOf(seatAssignments.toMap()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp)
    ) {
        // Indicadores de estado
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            StatusIndicator(label = "Disponible", iconRes = R.drawable.disponible)
            StatusIndicator(label = "Ocupado", iconRes = R.drawable.reservado)
            StatusIndicator(label = "Seleccionado", iconRes = R.drawable.seleccionado)
        }

        // Renderizado de asientos con formato de van
        vanRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                row.forEach { seatId ->
                    if (seatId.isEmpty()) {
                        Spacer(modifier = Modifier.width(32.dp)) // Espaciador para pasillo
                    } else {
                        SeatWithImage(
                            seatId = seatId,
                            assignedName = currentSeatAssignments[seatId],
                            isSpecial = seatId in specialSeats,
                            isSelected = currentSeatAssignments.containsKey(seatId),
                            onSeatSelected = {
                                if (!currentSeatAssignments.containsKey(seatId)) {
                                    currentSeatAssignments =
                                        currentSeatAssignments + (seatId to "Selected")
                                }
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp)) // Espaciado entre filas
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSeatConfirm,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar selección")
        }
    }
}

@Composable
fun SeatWithImage(
    seatId: String,
    assignedName: String?,
    isSpecial: Boolean = false,
    isSelected: Boolean = false,
    onSeatSelected: () -> Unit
) {
    val iconRes = when {
        isSelected -> R.drawable.seleccionado
        isSpecial -> R.drawable.conductor
        assignedName == null -> R.drawable.disponible
        else -> R.drawable.reservado
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(70.dp) // Tamaño aumentado para evitar cortes en el texto
            .clickable(enabled = assignedName == null && !isSpecial) { onSeatSelected() }
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )
        Text(
            text = assignedName ?: seatId,
            fontSize = 14.sp, // Aumentar el tamaño de fuente si es necesario
            color = Color.Black,
            textAlign = TextAlign.Center
        )
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
