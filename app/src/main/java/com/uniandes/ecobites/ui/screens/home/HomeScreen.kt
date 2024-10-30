package com.uniandes.ecobites.ui.screens.home

import RestaurantViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.uniandes.ecobites.ui.components.StoresGrid

import CategoriesRow
import OfferCarousel


import androidx.compose.runtime.Composable
import com.uniandes.ecobites.R
import com.uniandes.ecobites.ui.components.Store


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val restaurantViewModel: RestaurantViewModel = viewModel()
    val stores by restaurantViewModel.restaurants
    val isLoading by restaurantViewModel.isLoading

    // Cargar los datos de las tiendas al montar la pantalla
    LaunchedEffect(Unit) {
        restaurantViewModel.loadStores()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())            .padding(16.dp)
    ) {
        // Top Location and Search Bar Section
        TopSection()
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            // Mostrar indicador de carga mientras se obtienen los datos
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Mostrar los datos una vez que estén listos



//         Offer Carousel Section
            OfferCarousel()

            Spacer(modifier = Modifier.height(16.dp))

            // Categories Filter Section
            CategoriesRow()

            Spacer(modifier = Modifier.height(16.dp))

            // Stores/Restaurants Grid Section
            StoresGrid(navController = navController, restaurants = stores)

        }
    }
}


