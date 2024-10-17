package com.uniandes.ecobites.ui.screens.home

import StoresGrid
import CategoriesRow
import OfferCarousel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Location and Search Bar Section
        TopSection()

        Spacer(modifier = Modifier.height(16.dp))

//         Offer Carousel Section
        OfferCarousel()

        Spacer(modifier = Modifier.height(16.dp))

        // Categories Filter Section
        CategoriesRow()

        Spacer(modifier = Modifier.height(16.dp))

        // Stores/Restaurants Grid Section
        StoresGrid(navController = navController)
    }
}