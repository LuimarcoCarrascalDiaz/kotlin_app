package com.uniandes.ecobites.ui.navigation

import RestaurantMapScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.room.Room
import com.uniandes.ecobites.ui.components.BiometricAuth
import com.uniandes.ecobites.ui.components.NavBar
import com.uniandes.ecobites.ui.data.MenuDatabase
import com.uniandes.ecobites.ui.data.supabase
import com.uniandes.ecobites.ui.screens.*
import com.uniandes.ecobites.ui.screens.home.HomeScreen
import com.uniandes.ecobites.ui.screens.store.StoreDetailsScreen
import io.github.jan.supabase.auth.auth
import android.widget.Toast
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

@Composable
fun NavigationHost(navController: NavHostController, biometricAuth: BiometricAuth) {
    NavHost(navController = navController, startDestination = "login") {
        // Login Screen
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                navController = navController,
                biometricAuth = biometricAuth
            )
        }

        // Sign-Up Screen
        composable("signup") {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        // Main Content - only shows NavBar after login
        composable("home") {
            Scaffold(
                bottomBar = {
                    NavBar(navController = navController)
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    HomeScreen(navController)
                }
            }
        }

        // Function to check network connectivity
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }

        composable("cart") {
            val context = LocalContext.current
            val user = supabase.auth.currentUserOrNull()
            val userId = user?.id

            if (isNetworkAvailable(context)) {
                Scaffold(
                    bottomBar = {
                        NavBar(navController = navController)
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (userId != null) {
                            CartScreen(userId = userId)
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Sin conexión, intente más tarde", Toast.LENGTH_SHORT).show()
            }
        }

        composable("orders") {
            Scaffold(
                bottomBar = {
                    NavBar(navController = navController)
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    OrdersScreen(navController)
                }
            }
        }

        composable("profile") {
            Scaffold(
                bottomBar = {
                    NavBar(navController = navController)
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    ProfileScreen(navController = navController)
                }
            }
        }

        composable("store/{storeName}") { backStackEntry ->
            val storeName = backStackEntry.arguments?.getString("storeName")
            Scaffold(
                bottomBar = {
                    NavBar(navController = navController)
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    val user = supabase.auth.currentUserOrNull()
                    val userId = user?.id
                    StoreDetailsScreen(storeName ?: "", userId = userId!!)
                }
            }
        }

        composable("location") {
            Scaffold(
                bottomBar = {
                    NavBar(navController = navController)
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    RestaurantMapScreen()
                }
            }
        }

        composable("storage") {
            val menuDatabase = Room.databaseBuilder(
                LocalContext.current,
                MenuDatabase::class.java,
                "menu.db"
            ).build()
            Scaffold(
                bottomBar = {
                    NavBar(navController = navController)
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    StorageScreen(menuDatabase = menuDatabase)
                }
            }
        }


    }
}
