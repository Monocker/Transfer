package com.example.transfer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.transfer.R
import com.example.transfer.components.home.CardComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onCardClick: (String, Int, String) -> Unit) {
    var selectedCity by remember { mutableStateOf<String?>(null) }
    var hotels by remember { mutableStateOf(listOf<String>()) }
    var selectedHotel by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Transfers") }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            // Filtros horizontales
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Filtro de Ciudad
                DropdownSelector(
                    label = "City",
                    options = listOf("Cancun", "Puerto Morelos", "Playa del Carmen"),
                    selectedOption = selectedCity,
                    onOptionSelected = { city ->
                        selectedCity = city
                        hotels = getHotelsForCity(city)
                        selectedHotel = null // Reiniciar el hotel seleccionado
                    },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )

                // Filtro de Hotel
                if (selectedCity != null) {
                    DropdownSelector(
                        label = "Hotel",
                        options = hotels,
                        selectedOption = selectedHotel,
                        onOptionSelected = { hotel ->
                            selectedHotel = hotel
                        },
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                }

                // Selector de Fecha
                DatePicker(
                    selectedDate = selectedDate,
                    onDateSelected = { date ->
                        selectedDate = date
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Lista de Tarjetas
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
            ) {
                items(5) { // Ejemplo estático
                    CardComponent(
                        imageRes = R.drawable.bus_image,
                        title = "PRIVATE TRANSFER",
                        location = "Cancún, Quintana Roo. MX 77500",
                        price = "35 USD",
                        onClick = {
                            onCardClick(
                                "PRIVATE TRANSFER",
                                R.drawable.bus_image,
                                "Llegar 20 minutos antes de la hora programada. Si desea cancelar la cita, hágalo con un día de antelación."
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selectedOption ?: "Select $label")
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
                    text = { Text(option) }
                )
            }
        }
    }
}

@Composable
fun DatePicker(selectedDate: String, onDateSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(text = "Pick Up Date", style = MaterialTheme.typography.bodyMedium)
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selectedDate.ifEmpty { "Select Date" })
        }

        // Simulación de un calendario
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf("2024-11-28", "2024-11-29", "2024-11-30").forEach { date ->
                DropdownMenuItem(
                    onClick = {
                        onDateSelected(date)
                        expanded = false
                    },
                    text = { Text(date) }
                )
            }
        }
    }
}

// Simulación de hoteles disponibles por ciudad
fun getHotelsForCity(city: String): List<String> {
    return when (city) {
        "Cancun" -> listOf("Hotel Cancun A", "Hotel Cancun B", "Hotel Cancun C")
        "Puerto Morelos" -> listOf("Hotel PM A", "Hotel PM B")
        "Playa del Carmen" -> listOf("Hotel PDC A", "Hotel PDC B", "Hotel PDC C")
        else -> emptyList()
    }
}
