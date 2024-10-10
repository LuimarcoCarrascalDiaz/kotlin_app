package com.uniandes.ecobites

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.uniandes.ecobites.ui.SplashScreen
import com.uniandes.ecobites.ui.navigation.NavigationHost
import com.uniandes.ecobites.ui.theme.AppTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()  // Create the NavController
    var showSplashScreen by remember { mutableStateOf(true) }

    // Show splash screen for 3 seconds before navigating to the main content
    LaunchedEffect(Unit) {
        delay(3000) // 3 seconds delay
        showSplashScreen = false
    }

    if (showSplashScreen) {
        SplashScreen()
    } else {
        NavigationHost(navController = navController)  // Pass the NavController here
    }
}
