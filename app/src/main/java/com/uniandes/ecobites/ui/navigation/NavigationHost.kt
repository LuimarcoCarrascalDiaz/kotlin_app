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
import com.uniandes.ecobites.ui.screens.ImageCacheScreen  // Importa la pantalla de caching
import io.github.jan.supabase.auth.auth
import android.widget.Toast
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.uniandes.ecobites.R
import com.uniandes.ecobites.ui.screens.restaurants.HornitosScreen

@Composable
fun NavigationHost(navController: NavHostController, biometricAuth: BiometricAuth) {
    NavHost(navController = navController, startDestination = "login") {

        // Pantalla de login
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

        // Pantalla de registro
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

        // Pantalla principal
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

        // Verificación de conexión a Internet
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }

        // Pantalla de carrito
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
                Image(
                    painter = painterResource(id = R.drawable.carticon),  // Reemplaza "img" con el nombre de tu imagen en drawable
                    contentDescription = "Descripción de la imagen",
                    modifier = Modifier.size(500.dp) // Ajusta el tamaño según tus necesidades
                )
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

        // Pantalla de perfil con botón de caching
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
        composable("hornitos") {
            val context= LocalContext.current
            HornitosScreen(context=context)
        }

        // Pantalla de caching
        composable("caching") {  // Agregamos la nueva ruta "caching"
            Scaffold(
                bottomBar = {
                    NavBar(navController = navController)
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    ImageCacheScreen()  // Llamada a la pantalla de caching
                }
            }
        }
    }
}
