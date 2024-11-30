package com.example.transfer.ui.screens

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.transfer.components.home.CardComponent
import com.example.transfer.data.model.Reservation
import com.example.transfer.viewmodel.ReservationViewModel
import java.util.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, viewModel: ReservationViewModel) {
    // Obtén el contexto
    val context = LocalContext.current

    // Inicializar reservas al iniciar
    LaunchedEffect(Unit) {
        viewModel.initializeReservations(context)
    }

    // Variables de estado
    var selectedZone by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedAgency by rememberSaveable { mutableStateOf<String?>(null) }
    var hotels by rememberSaveable { mutableStateOf(listOf<String>()) }
    var selectedHotel by rememberSaveable { mutableStateOf<String?>(null) }
    var stores by rememberSaveable { mutableStateOf(listOf<String>()) }
    var selectedStore by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedModel by rememberSaveable { mutableStateOf<String?>(null) }
    var pickUpTime by rememberSaveable { mutableStateOf("11:30 AM") }
    var date by rememberSaveable { mutableStateOf("") }
    var pax by rememberSaveable { mutableStateOf("") }
    var adults by rememberSaveable { mutableStateOf("") }
    var children by rememberSaveable { mutableStateOf("") }
    var clientName by rememberSaveable { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Mapeo de hoteles con sus horarios de Pick-Up
    val hotelPickUpTimes = mapOf(
        "Hotel A" to "9:00 AM",
        "Hotel B" to "11:00 AM",
        "Hotel C" to "12:00 PM",
        "Hotel D" to "10:00 AM",
        "Hotel E" to "1:00 PM",
        "Hotel F" to "10:00 AM",
        "Hotel G" to "1:00 PM",
        "Hotel H" to "2:00 PM"
    )

    // Observa las reservas desde el ViewModel
    val reservations = viewModel.reservations

    // Generar transferencias actualizadas basadas en las reservas
    val updatedTransfers by remember(reservations) {
        derivedStateOf {
            reservations.map { reservation ->
                TransferData(
                    title = reservation.model,
                    location = reservation.zone,
                    totalSeats = reservation.totalSeats,
                    availableSeats = reservation.availableSeats,
                    reservedSeats = reservation.reservedSeats,
                    seatStates = reservation.seatStates
                )
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

    val snackbarHostState = remember { SnackbarHostState() }

    // Observar si la reserva ha sido finalizada y resetear campos
    LaunchedEffect(viewModel.reservationFinalized) {
        if (viewModel.reservationFinalized) {
            // Resetear todos los campos
            selectedZone = null
            selectedAgency = null
            hotels = listOf()
            selectedHotel = null
            stores = listOf()
            selectedStore = null
            selectedModel = null
            pickUpTime = "11:30 AM"
            date = ""
            pax = ""
            adults = ""
            children = ""
            clientName = ""
            viewModel.resetReservationFinalized()
        }
    }

    // Mostrar Snackbar si hay un mensaje de error
    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(errorMessage)
            errorMessage = "" // Resetear mensaje de error después de mostrarlo
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterDropdown(
                    label = "Zona",
                    options = listOf("Cancún", "Puerto Morelos", "Riviera Maya"),
                    selectedOption = selectedZone,
                    onOptionSelected = { zone ->
                        selectedZone = zone
                        selectedAgency = null
                        selectedHotel = null
                        selectedStore = null
                        hotels = getHotelsForZone(zone)
                        stores = getStoresForZone(zone)
                    },
                    modifier = Modifier.weight(1f)
                )

                if (selectedZone != null) {
                    FilterDropdown(
                        label = "Agencia",
                        options = listOf("Agencia 1", "Agencia 2", "Agencia 3"),
                        selectedOption = selectedAgency,
                        onOptionSelected = { agency ->
                            selectedAgency = agency
                            selectedHotel = null // Reiniciar hotel seleccionado
                        },
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
                        options = stores,
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
                                    val selectedDate = Calendar.getInstance()
                                    selectedDate.set(selectedYear, selectedMonth, selectedDay)
                                    if (selectedDate.after(calendar)) {
                                        date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                                    } else {
                                        errorMessage = "Por favor, selecciona una fecha futura."
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
                    onValueChange = { pax = it },
                    modifier = Modifier.weight(1f)
                )
                PaxInputField(
                    label = "Adultos",
                    value = adults,
                    onValueChange = { adults = it },
                    modifier = Modifier.weight(1f)
                )
                PaxInputField(
                    label = "Menores",
                    value = children,
                    onValueChange = { children = it },
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
                            // Validar que los campos obligatorios estén llenos
                            val missingFields = mutableListOf<String>()
                            if (selectedZone == null) missingFields.add("Zona")
                            if (selectedAgency == null) missingFields.add("Agencia")
                            if (selectedHotel == null) missingFields.add("Hotel")
                            if (selectedStore == null) missingFields.add("Tienda")
                            if (date.isEmpty()) missingFields.add("Fecha")
                            if (clientName.isEmpty()) missingFields.add("Nombre del Cliente")
                            // Agrega otros campos si es necesario

                            if (missingFields.isEmpty()) {
                                // Crear una instancia de Reservation
                                val reservation = Reservation(
                                    id = "",
                                    zone = selectedZone ?: "",
                                    agency = selectedAgency ?: "",
                                    hotel = selectedHotel ?: "",
                                    store = selectedStore ?: "",
                                    pickUpTime = pickUpTime,
                                    date = date,
                                    model = transfer.title,
                                    pax = pax.toIntOrNull() ?: 0,
                                    adults = adults.toIntOrNull() ?: 0,
                                    children = children.toIntOrNull() ?: 0,
                                    clientName = clientName,
                                    seats = emptyList(),
                                    totalSeats = transfer.totalSeats,
                                    reservedSeats = transfer.reservedSeats,
                                    availableSeats = transfer.availableSeats,
                                    seatStates = transfer.seatStates ?: mutableMapOf()
                                )
                                // Guardar la reserva actual en el ViewModel
                                viewModel.currentReservation = reservation
                                // Navegar a SeatSelectionScreen
                                navController.navigate("seat_selection")
                            } else {
                                // Mostrar un mensaje de error indicando los campos faltantes
                                errorMessage =
                                    "Por favor, completa los campos obligatorios: ${missingFields.joinToString(", ")}"
                                Log.e("HomeScreen", errorMessage)
                            }
                        }
                    )
                }
            }

        }
    }
}

// Función para obtener las tiendas según la zona
fun getStoresForZone(zone: String): List<String> = when (zone) {
    "Cancún" -> listOf("Tienda Plaza 28", "Opalo Mercado 28")
    "Puerto Morelos" -> listOf("Restaurante Matea", "Tienda Masco Viejo")
    "Riviera Maya" -> listOf("Tienda Mayan Market Tulum", "Canoto Nohoch")
    else -> emptyList()
}

// Función para obtener los hoteles según la zona
fun getHotelsForZone(zone: String): List<String> = when (zone) {
    "Cancún" -> listOf("Hotel A", "Hotel B", "Hotel C")
    "Puerto Morelos" -> listOf("Hotel D", "Hotel E")
    "Riviera Maya" -> listOf("Hotel F", "Hotel G", "Hotel H")
    else -> emptyList()
}

// Componente de Dropdown
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
fun PaxInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.filter { char -> char.isDigit() }) },
        label = { Text(label) },
        modifier = modifier.padding(horizontal = 4.dp)
    )
}

// Modelo de datos para Transferencia
data class TransferData(
    val title: String,
    val location: String,
    val totalSeats: Int,
    val availableSeats: Int,
    val reservedSeats: Int,
    val seatStates: MutableMap<String, String>? = null
)
