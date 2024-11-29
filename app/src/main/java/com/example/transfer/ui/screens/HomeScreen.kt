package com.example.transfer.ui.screens

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.transfer.components.home.CardComponent
import com.example.transfer.viewmodel.ReservationViewModel
import java.util.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, viewModel: ReservationViewModel) {
    // Obtén el contexto
    val context = LocalContext.current

    // Si quieres mantener el `LaunchedEffect` inicial para cargar una sola vez:
    LaunchedEffect(Unit) {
        viewModel.loadReservationsFromPreferences(
            context.getSharedPreferences("ReservationPrefs", Context.MODE_PRIVATE)
        )
    }


    var selectedZone by remember { mutableStateOf<String?>(null) }
    var selectedAgency by remember { mutableStateOf<String?>(null) }
    var hotels by remember { mutableStateOf(listOf<String>()) }
    var selectedHotel by remember { mutableStateOf<String?>(null) }
    var selectedStore by remember { mutableStateOf<String?>(null) }
    var selectedModel by remember { mutableStateOf<String?>(null) }
    var pickUpTime by remember { mutableStateOf("11:30 AM") }
    var date by remember { mutableStateOf("") }
    var pax by remember { mutableStateOf("") }
    var adults by remember { mutableStateOf("") }
    var children by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }

    // Mapeo de hoteles con sus horarios de Pick-Up
    val hotelPickUpTimes = mapOf(
        "Hotel A" to "9:00 AM",
        "Hotel B" to "11:00 AM",
        "Hotel C" to "12:00 PM"
    )

    // Generar transferencias iniciales (3 tarjetas por zona y modelo)
    val initialTransfers = remember {
        mutableStateListOf(
            TransferData("Van", "Cancún", 15, 15, 0),
            TransferData("Transit", "Cancún", 12, 12, 0),
            TransferData("Rifter", "Cancún", 10, 10, 0),
            TransferData("Van", "Puerto Morelos", 15, 15, 0),
            TransferData("Transit", "Puerto Morelos", 12, 12, 0),
            TransferData("Rifter", "Puerto Morelos", 10, 10, 0),
            TransferData("Van", "Rivera Maya", 15, 15, 0),
            TransferData("Transit", "Rivera Maya", 12, 12, 0),
            TransferData("Rifter", "Rivera Maya", 10, 10, 0)
        )
    }

    // Observa las reservas desde el ViewModel
    val reservations = viewModel.reservations

    val updatedTransfers by remember(reservations) {
        derivedStateOf {
            initialTransfers.map { transfer ->
                val matchingReservation = reservations.find { reservation ->
                    reservation.title == transfer.title && reservation.zone == transfer.location
                }
                if (matchingReservation != null) {
                    Log.d(
                        "HomeScreen",
                        "Matching reservation found: ${matchingReservation.title} (${matchingReservation.zone}), " +
                                "Available: ${matchingReservation.availableSeats}, Reserved: ${matchingReservation.reservedSeats}"
                    )
                    transfer.copy(
                        availableSeats = matchingReservation.availableSeats,
                        reservedSeats = matchingReservation.reservedSeats
                    )
                } else {
                    Log.d(
                        "HomeScreen",
                        "No matching reservation for: ${transfer.title} in ${transfer.location}"
                    )
                    transfer
                }
            }
        }
    }

// Filtrar transferencias dinámicamente
    val filteredTransfers = updatedTransfers.filter { transfer ->
        (selectedModel == null || transfer.title == selectedModel) &&
                (selectedZone == null || transfer.location == selectedZone) &&
                transfer.availableSeats > 0
    }



    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterDropdown(
                label = "Zona",
                options = listOf("Cancún", "Puerto Morelos", "Rivera Maya"),
                selectedOption = selectedZone,
                onOptionSelected = { zone ->
                    selectedZone = zone
                    selectedAgency = null
                    selectedHotel = null
                    hotels = getHotelsForZone(zone)
                },
                modifier = Modifier.weight(1f)
            )

            if (selectedZone != null) {
                FilterDropdown(
                    label = "Agencia",
                    options = listOf("Agencia 1", "Agencia 2", "Agencia 3"),
                    selectedOption = selectedAgency,
                    onOptionSelected = { agency -> selectedAgency = agency },
                    modifier = Modifier.weight(1f)
                )
            }

            if (selectedAgency != null) {
                FilterDropdown(
                    label = "Hotel",
                    options = hotels,
                    selectedOption = selectedHotel,
                    onOptionSelected = { hotel ->
                        selectedHotel = hotel
                        pickUpTime = hotelPickUpTimes[hotel] ?: "11:30 AM"
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            if (selectedHotel != null) {
                FilterDropdown(
                    label = "Tienda",
                    options = listOf("Tienda 1", "Tienda 2", "Tienda 3"),
                    selectedOption = selectedStore,
                    onOptionSelected = { store -> selectedStore = store },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = pickUpTime,
                onValueChange = {},
                label = { Text("Pick Up", fontWeight = FontWeight.Bold) },
                readOnly = true,
                enabled = false,
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledTextColor = Color.Black,
                    disabledBorderColor = Color.Black,
                    disabledLabelColor = Color.Black
                )
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        DatePickerDialog(
                            context,
                            { _, selectedYear, selectedMonth, selectedDay ->
                                if (Calendar.getInstance().apply {
                                        set(selectedYear, selectedMonth, selectedDay)
                                    }.after(calendar)) {
                                    date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                                }
                            },
                            year,
                            month,
                            day
                        ).apply {
                            datePicker.minDate = calendar.timeInMillis + 24 * 60 * 60 * 1000 // Solo fechas futuras
                        }.show()
                    }
            ) {
                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    label = { Text("Fecha", fontWeight = FontWeight.Bold) },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (selectedZone != null) {
                FilterDropdown(
                    label = "Modelo",
                    options = listOf("Van", "Transit", "Rifter"),
                    selectedOption = selectedModel,
                    onOptionSelected = { model -> selectedModel = model },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PaxInputField(
                label = "Pax",
                value = pax,
                onValueChange = { pax = it.toString() },
                modifier = Modifier.weight(1f)
            )
            PaxInputField(
                label = "Adultos",
                value = adults,
                onValueChange = { adults = it.toString() },
                modifier = Modifier.weight(1f)
            )
            PaxInputField(
                label = "Menores",
                value = children,
                onValueChange = { children = it.toString() },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = clientName,
            onValueChange = { clientName = it },
            label = { Text("Nombre del Cliente") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Log.d("HomeScreen", "Updated Transfers: $updatedTransfers")

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredTransfers) { transfer ->
                CardComponent(
                    title = transfer.title,
                    zone = transfer.location,
                    totalSeats = transfer.totalSeats,
                    availableSeats = transfer.availableSeats,
                    reservedSeats = transfer.reservedSeats,
                    onClick = {
                        navController.navigate("seat_selection/${transfer.title}/${transfer.location}/${transfer.totalSeats}")
                    }
                )
            }
        }


    }
}





// Dropdown Component
@Composable
fun FilterDropdown(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .background(Color.Gray, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(8.dp)
        ) {
            Text(
                text = selectedOption ?: "Seleccionar $label",
                color = Color.White
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onOptionSelected(option)
                }, text = { Text(option) })
            }
        }
    }
}


@Composable
fun PassengerDetails(
    passengerDetails: Passenger,
    onDetailsChange: (Passenger) -> Unit
) {
    var name by remember { mutableStateOf(passengerDetails.name) }
    var age by remember { mutableStateOf(passengerDetails.age) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("Información del Pasajero", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                onDetailsChange(Passenger(name, age))
            },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = age,
            onValueChange = {
                age = it
                onDetailsChange(Passenger(name, age))
            },
            label = { Text("Edad") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
@Composable
fun PaxInputField(label: String, value: String, onValueChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = { onValueChange(it.toIntOrNull() ?: 0) },
        label = { Text(label) },
        modifier = modifier.padding(horizontal = 4.dp) // Espaciado opcional
    )
}




// Transfer Data Model
data class TransferData(
    val title: String,
    val location: String,
    val totalSeats: Int,
    val availableSeats: Int,
    val reservedSeats: Int
)

data class Passenger(val name: String, val age: String)

fun getHotelsForZone(zone: String): List<String> = when (zone) {
    "Cancún" -> listOf("Hotel A", "Hotel B", "Hotel C")
    "Puerto Morelos" -> listOf("Hotel D", "Hotel E")
    "Playa del Carmen" -> listOf("Hotel F", "Hotel G", "Hotel H")
    else -> emptyList()
}