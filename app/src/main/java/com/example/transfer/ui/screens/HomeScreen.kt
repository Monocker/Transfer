package com.example.transfer.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.transfer.R
import com.example.transfer.components.home.CardComponent
import java.util.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    var selectedZone by remember { mutableStateOf<String?>(null) }
    var selectedAgency by remember { mutableStateOf<String?>(null) }
    var hotels by remember { mutableStateOf(listOf<String>()) }
    var selectedHotel by remember { mutableStateOf<String?>(null) }
    var selectedStore by remember { mutableStateOf<String?>(null) }
    val fixedTime = "11:30 AM"
    var date by remember { mutableStateOf("") }
    var adults by remember { mutableStateOf(0) }
    var children by remember { mutableStateOf(0) }
    var clientName by remember { mutableStateOf("") } // Campo para Nombre y Apellido del Cliente

    // Lista de transferencias (filtradas dinámicamente)
    val allTransfers = listOf(
        TransferData("PRIVATE TRANSFER", "Cancún", 12, 12),
        TransferData("VIP TRANSFER", "Puerto Morelos", 8, 8),
        TransferData("SHARED TRANSFER", "Playa del Carmen", 15, 15),
        TransferData("LUXURY TRANSFER", "Cancún", 5, 5),
        TransferData("FAMILY TRANSFER", "Playa del Carmen", 10, 10)
    )

    val filteredTransfers = allTransfers.filter { transfer ->
        (selectedZone == null || transfer.location == selectedZone) &&
                (adults + children <= transfer.availableSeats)
    }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        // Filtros organizados en un diseño responsivo
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterDropdown(
                label = "Zona",
                options = listOf("Cancún", "Puerto Morelos", "Playa del Carmen"),
                selectedOption = selectedZone,
                onOptionSelected = { zone ->
                    selectedZone = zone
                    selectedAgency = null
                    selectedStore = null
                    hotels = getHotelsForZone(zone)
                    selectedHotel = null
                },
                modifier = Modifier.weight(1f) // Para asegurar proporciones
            )

            if (selectedZone != null) {
                FilterDropdown(
                    label = "Agencia",
                    options = listOf("Agencia 1", "Agencia 2", "Agencia 3"),
                    selectedOption = selectedAgency,
                    onOptionSelected = { agency ->
                        selectedAgency = agency
                        selectedHotel = null
                        selectedStore = null
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
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            if (selectedHotel != null) {
                FilterDropdown(
                    label = "Tienda",
                    options = listOf("Tienda 1", "Tienda 2", "Tienda 3"),
                    selectedOption = selectedStore,
                    onOptionSelected = { store ->
                        selectedStore = store
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Pick Up y Fecha en una fila horizontal
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = fixedTime,
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
                                date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                            },
                            year,
                            month,
                            day
                        ).show()
                    }
            ) {
                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    label = { Text("Fecha", fontWeight = FontWeight.Bold) },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = Color.Black,
                        disabledBorderColor = Color.Black,
                        disabledLabelColor = Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campos para Pax, Adultos y Menores
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PaxInputField(
                label = "Adultos",
                value = adults,
                onValueChange = { adults = it },
                modifier = Modifier.weight(1f) // Distribuye el ancho equitativamente
            )
            PaxInputField(
                label = "Menores",
                value = children,
                onValueChange = { children = it },
                modifier = Modifier.weight(1f) // Distribuye el ancho equitativamente
            )
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Campo para Nombre y Apellido del Cliente
        OutlinedTextField(
            value = clientName,
            onValueChange = { clientName = it },
            label = { Text("Nombre y Apellido del Cliente") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de transfers filtrados
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredTransfers) { transfer ->
                CardComponent(
                    imageRes = R.drawable.bus_image,
                    title = transfer.title,
                    location = transfer.location,
                    price = "${transfer.price} USD",
                    description = "Asientos disponibles: ${transfer.availableSeats}",
                    onClick = {
                        navController.navigate("seat_selection/${transfer.title}")
                    }
                )
            }
        }
    }
}



@Composable
fun FilterDropdown(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.width(150.dp)) { // Ancho fijo para todas las cajas
        Text(text = label, fontSize = 14.sp, color = Color.Black)
        Box(
            modifier = Modifier
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                .clickable { expanded = !expanded }
                .padding(8.dp)
        ) {
            Text(
                text = selectedOption ?: "Seleccionar $label",
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis // Muestra puntos suspensivos si el texto es largo
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    text = {
                        Text(
                            text = option,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis // Asegura que los textos largos también se trunquen aquí
                        )
                    }
                )
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
fun PaxInputField(label: String, value: Int, onValueChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = { onValueChange(it.toIntOrNull() ?: 0) },
        label = { Text(label) },
        modifier = modifier.padding(horizontal = 4.dp) // Espaciado opcional
    )
}




data class TransferData(val title: String, val location: String, val price: Int, val availableSeats: Int) {
}

data class Passenger(val name: String, val age: String)

fun getHotelsForZone(zone: String): List<String> = when (zone) {
    "Cancún" -> listOf("Hotel A", "Hotel B", "Hotel C")
    "Puerto Morelos" -> listOf("Hotel D", "Hotel E")
    "Playa del Carmen" -> listOf("Hotel F", "Hotel G", "Hotel H")
    else -> emptyList()
}