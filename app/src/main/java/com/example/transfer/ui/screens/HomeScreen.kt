package com.example.transfer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.transfer.common.Navbar
import com.example.transfer.components.home.CardComponent
import com.example.transfer.R

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf("Home") }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(5) { /
                CardComponent(
                    imageRes = R.drawable.bus_image, 
                    title = "PRIVATE TRANSFER - REGULAR VAN",
                    location = "Cancún, Quintana Roo. MX 77500",
                    price = "35 USD"
                )
            }
        }
        Navbar(selectedItem = selectedTab, onItemSelected = { selectedTab = it })
    }
}
