package com.example.transfer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.transfer.navigation.AppNavigation
import com.example.transfer.ui.theme.TransferTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TransferTheme {
                AppNavigation()
            }
        }
    }
}
