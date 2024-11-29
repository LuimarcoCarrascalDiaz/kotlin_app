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
import com.uniandes.ecobites.ui.components.BiometricAuth
import com.uniandes.ecobites.ui.navigation.NavigationHost
import com.uniandes.ecobites.ui.theme.AppTheme
import kotlinx.coroutines.delay
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val biometricAuth = BiometricAuth(this)
        FirebaseApp.initializeApp(this)
        // Opcional: Inicializar Firebase Analytics
        val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        // Inicialización de Firestore
        val firestore = FirebaseFirestore.getInstance()
        setContent {
            AppTheme {
                MyApp(biometricAuth)
            }
        }
    }
}

@Composable
fun MyApp(biometricAuth: BiometricAuth) {
    val navController = rememberNavController()  // Create the NavController
    var showSplashScreen by remember { mutableStateOf(true) }

    // Show splash screen for 1.5 seconds before navigating to the main content
    LaunchedEffect(Unit) {
        delay(1500)
        showSplashScreen = false
    }

    if (showSplashScreen) {
        SplashScreen()
    } else {
        NavigationHost(navController = navController, biometricAuth = biometricAuth)  // Pass the NavController here
    }
}
