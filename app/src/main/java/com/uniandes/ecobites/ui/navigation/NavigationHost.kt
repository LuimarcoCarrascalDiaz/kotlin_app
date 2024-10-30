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
import com.uniandes.ecobites.ui.screens.LoginScreen
import com.uniandes.ecobites.ui.screens.SignUpScreen
import com.uniandes.ecobites.ui.screens.ProfileScreen
import com.uniandes.ecobites.ui.screens.home.HomeScreen
import com.uniandes.ecobites.ui.screens.OrdersScreen
import com.uniandes.ecobites.ui.screens.CartScreen
import com.uniandes.ecobites.ui.screens.StorageScreen
import com.uniandes.ecobites.ui.screens.store.StoreDetailsScreen
import io.github.jan.supabase.auth.auth

@Composable
fun NavigationHost(navController: NavHostController, biometricAuth: BiometricAuth) {

    NavHost(navController = navController, startDestination = "login") {
        // Login Screen
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }  // Remove login screen from backstack
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
                        popUpTo("signup") { inclusive = true }  // Remove signup screen from backstack
                    }
                },
                navController = navController
            )
        }

        // Main Content - only shows NavBar after login
        composable("home") {
            Scaffold(
                bottomBar = {
                    NavBar(navController = navController)  // NavBar is only shown after login
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    HomeScreen(navController)
                }
            }
        }

        composable("cart") {
            val user = supabase.auth.currentUserOrNull()
            val userId = user?.id
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
                    ProfileScreen()
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
                    NavBar(navController = navController)  // NavBar is shown here too
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    RestaurantMapScreen()  // Pantalla de mapa
                }
            }
        }
        // Nueva pantalla "storage" agregada al NavHost
        composable("storage") {
            //Crear instancia de MenuDatabase directamente aquí
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
