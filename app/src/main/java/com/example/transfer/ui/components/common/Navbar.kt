    package com.example.transfer.common

    import androidx.compose.foundation.layout.*
    import androidx.compose.material3.*
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Home
    import androidx.compose.material.icons.filled.CalendarToday
    import androidx.compose.material.icons.filled.Person

    @Composable
    fun Navbar(selectedItem: String, onItemSelected: (String) -> Unit) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.height(60.dp)
        ) {
            val items = listOf("Home", "Schedules", "Profile")
            val icons = listOf(Icons.Default.Home, Icons.Default.CalendarToday, Icons.Default.Person)
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = { Icon(icons[index], contentDescription = item) },
                    label = { Text(item, fontSize = 10.sp) },
                    selected = selectedItem == item,
                    onClick = { onItemSelected(item) }
                )
            }
        }
    }
