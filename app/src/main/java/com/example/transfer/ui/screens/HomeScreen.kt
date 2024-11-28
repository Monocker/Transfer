package com.example.transfer.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen() {
    // Estados para cada campo del formulario
    var selectedZone by remember { mutableStateOf("") }
    var selectedHotel by remember { mutableStateOf("") }
    var selectedAgency by remember { mutableStateOf("") }
    var selectedStore by remember { mutableStateOf("") }
    var pickupTime by remember { mutableStateOf("10:00 AM") } // Hora fija
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var clientFirstName by remember { mutableStateOf("") }
    var clientLastName by remember { mutableStateOf("") }
    var clientYearOfBirth by remember { mutableStateOf("") }

    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Campo de selección de Zona
        DropdownMenuField(
            label = "Zona",
            options = listOf("Cancún", "Riviera Maya", "Puerto Morelos", "Tulum", "Playa Mujeres"),
            selectedOption = selectedZone,
            onOptionSelected = { selectedZone = it }
        )

        // Campo de selección de Hotel
        DropdownMenuField(
            label = "Hotel",
            options = listOf("Hotel A", "Hotel B", "Hotel C"), // Opciones de ejemplo
            selectedOption = selectedHotel,
            onOptionSelected = { selectedHotel = it }
        )

        // Campo de selección de Agencia
        DropdownMenuField(
            label = "Agencia",
            options = listOf("Agencia X", "Agencia Y", "Agencia Z"), // Opciones de ejemplo
            selectedOption = selectedAgency,
            onOptionSelected = { selectedAgency = it }
        )

        // Campo de selección de Tienda de Destino
        DropdownMenuField(
            label = "Tienda de Destino",
            options = listOf("Tienda 1", "Tienda 2", "Tienda 3"), // Opciones de ejemplo
            selectedOption = selectedStore,
            onOptionSelected = { selectedStore = it }
        )

        // Campo de selección de Hora de Recogida (no modificable)
        OutlinedTextField(
            value = pickupTime,
            onValueChange = {},
            label = { Text("Hora de Recogida") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        // Campo de selección de Fecha
        OutlinedTextField(
            value = selectedDate.format(dateFormatter),
            onValueChange = {},
            label = { Text("Fecha") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
        )

        // Mostrar DatePickerDialog cuando showDatePicker es verdadero
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(
                    state = rememberDatePickerState(initialSelectedDateMillis = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000),
                    onDateChange = { millis ->
                        selectedDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                    }
                )
            }
        }

        // Campos de datos del cliente
        OutlinedTextField(
            value = clientFirstName,
            onValueChange = { clientFirstName = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = clientLastName,
            onValueChange = { clientLastName = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = clientYearOfBirth,
            onValueChange = { clientYearOfBirth = it },
            label = { Text("Año de Nacimiento") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Botón de envío
        Button(
            onClick = {
                // Validar y procesar la información del formulario
                if (selectedZone.isNotEmpty() && selectedHotel.isNotEmpty() && selectedAgency.isNotEmpty() &&
                    selectedStore.isNotEmpty() && clientFirstName.isNotEmpty() && clientLastName.isNotEmpty() &&
                    clientYearOfBirth.isNotEmpty()
                ) {
                    Toast.makeText(context, "Reserva realizada con éxito", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reservar")
        }
    }
}

@Composable
fun DropdownMenuField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
