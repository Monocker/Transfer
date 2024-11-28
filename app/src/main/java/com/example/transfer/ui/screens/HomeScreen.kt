package com.example.transfer.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
fun HomeScreen() {
    val context = LocalContext.current

    // Estados para cada campo del formulario
    var selectedZone by remember { mutableStateOf<String?>(null) }
    var selectedHotel by remember { mutableStateOf<String?>(null) }
    var selectedAgency by remember { mutableStateOf<String?>(null) }
    var selectedStore by remember { mutableStateOf<String?>(null) }
    val pickupTime = "10:00 AM" // Hora fija
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var clientFirstName by remember { mutableStateOf("") }
    var clientLastName by remember { mutableStateOf("") }
    var clientYear by remember { mutableStateOf("") }

    // Listas de opciones predefinidas
    val zones = listOf("Cancún", "Riviera Maya", "Puerto Morelos", "Tulum", "Playa Mujeres")
    val hotels = listOf("Hotel A", "Hotel B", "Hotel C")
    val agencies = listOf("Agencia X", "Agencia Y", "Agencia Z")
    val stores = listOf("Tienda 1", "Tienda 2", "Tienda 3")

    // Función para restablecer los filtros
    fun resetFilters() {
        selectedZone = null
        selectedHotel = null
        selectedAgency = null
        selectedStore = null
    }

    // Formato de fecha
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Estado para el diálogo de selección de fecha
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Dropdowns para seleccionar zona, hotel, agencia y tienda
        DropdownSelector(
            label = "Zona",
            options = zones,
            selectedOption = selectedZone,
            onOptionSelected = { selectedZone = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DropdownSelector(
            label = "Hotel",
            options = hotels,
            selectedOption = selectedHotel,
            onOptionSelected = { selectedHotel = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DropdownSelector(
            label = "Agencia",
            options = agencies,
            selectedOption = selectedAgency,
            onOptionSelected = { selectedAgency = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DropdownSelector(
            label = "Tienda de Destino",
            options = stores,
            selectedOption = selectedStore,
            onOptionSelected = { selectedStore = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de texto para la hora de recogida (no editable)
        OutlinedTextField(
            value = pickupTime,
            onValueChange = {},
            label = { Text("Hora de Recogida") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Selector de fecha
        OutlinedTextField(
            value = selectedDate.format(dateFormatter),
            onValueChange = {},
            label = { Text("Fecha de Reserva") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
        )

        if (showDatePicker) {
            DatePickerDialog(
                initialDate = selectedDate,
                onDateSelected = {
                    selectedDate = it
                    showDatePicker = false
                },
                onDismissRequest = { showDatePicker = false }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Campos de texto para los datos del cliente
        OutlinedTextField(
            value = clientFirstName,
            onValueChange = { clientFirstName = it },
            label = { Text("Nombre del Cliente") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = clientLastName,
            onValueChange = { clientLastName = it },
            label = { Text("Apellido del Cliente") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = clientYear,
            onValueChange = { clientYear = it },
            label = { Text("Año de Nacimiento") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para restablecer filtros
        Button(
            onClick = { resetFilters() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Restablecer Filtros")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de tarjetas filtradas
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(5) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        Toast.makeText(context, "Tarjeta $index seleccionada", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tarjeta $index")
                        Text("Detalles de la tarjeta $index")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de reserva
        Button(
            onClick = {
                // Lógica para procesar la reserva
                Toast.makeText(context, "Reserva procesada", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reservar")
        }
    }
}
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val displayText = selectedOption ?: ""

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = displayText,
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(option)
                    expanded = false
                }) {
                    Text(text = option)
                }
            }
        }
    }
}