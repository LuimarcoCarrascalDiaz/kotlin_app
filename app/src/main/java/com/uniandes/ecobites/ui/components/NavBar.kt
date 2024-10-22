package com.uniandes.ecobites.ui.components

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.LocationOn  // Nuevo ícono de ubicación
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun NavBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.ShoppingCart, contentDescription = "Cart") },
            label = { Text("Cart") },
            selected = currentRoute == "cart",
            onClick = { navController.navigate("cart") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.List, contentDescription = "Orders") },
            label = { Text("Orders") },
            selected = currentRoute == "orders",
            onClick = { navController.navigate("orders") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute == "profile",
            onClick = { navController.navigate("profile") }
        )
        // Nuevo ítem de Location
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.LocationOn, contentDescription = "Location") },  // Ícono de ubicación
            label = { Text("Location") },
            selected = currentRoute == "location",
            onClick = { navController.navigate("location") }
        )
    }
}
