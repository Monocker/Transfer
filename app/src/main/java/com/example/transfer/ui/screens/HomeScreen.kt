package com.example.transfer.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.transfer.components.home.CardComponent
import com.example.transfer.R

@Composable
fun HomeScreen(onCardClick: (String, Int, String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(5) { // Datos estáticos de ejemplo
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
