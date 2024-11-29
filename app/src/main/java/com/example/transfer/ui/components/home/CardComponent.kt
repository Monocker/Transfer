package com.example.transfer.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CardComponent(
    title: String,
    zone: String,
    totalSeats: Int,
    availableSeats: Int,
    reservedSeats: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        // Modelo del vehículo
        Text(
            text = "Modelo: $title",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Zona
        Text(
            text = "Zona: $zone",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Asientos Totales
        Text(
            text = "Asientos Totales: $totalSeats",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Disponibles
        Text(
            text = "Disponibles: $availableSeats",
            fontSize = 14.sp,
            color = Color.Green
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Ocupados
        Text(
            text = "Ocupados: $reservedSeats",
            fontSize = 14.sp,
            color = Color.Red
        )

        // Divider entre elementos
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
