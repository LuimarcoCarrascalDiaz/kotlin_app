package com.uniandes.ecobites.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uniandes.ecobites.R
import androidx.navigation.NavController


// Sample data structure for orders
data class Order(
    val storeName: String,
    val deliveryDate: String,
    val storeLogo: Int  // Resource ID of the store logo
)


val pastOrders = listOf(
    Order("Pan Pa Ya!", "10/09", R.drawable.pan_pa_ya),
    Order("McDonalds", "10/09", R.drawable.mc_donalds),
    Order("Exito", "10/09", R.drawable.exito),
    Order("Hornitos", "10/09", R.drawable.hornitos))


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(navController: NavController) {
    // Mockup data

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orders") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back action */ }) {
                        Icon(imageVector = Icons.Rounded.KeyboardArrowLeft, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Keep saving money!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Button(
                    onClick = { navController.navigate("storage") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF907B00)  // Adjust the color as needed
                    )
                ) {
                    Text(text = "local storage")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(pastOrders) { order ->
                OrderItemCard(order)
            }
        }
    }
}

@Composable
fun OrderItemCard(order: Order) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display Store Logo
            Image(
                contentScale = ContentScale.Crop,
                painter = painterResource(order.storeLogo),
                contentDescription = "Store Logo",
                modifier = Modifier.size(100.dp).fillMaxSize().clip(MaterialTheme.shapes.medium)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = order.storeName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Delivered ${order.deliveryDate}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            IconButton(onClick = { /* Handle view details action */ }) {
                Icon(imageVector = Icons.Rounded.KeyboardArrowRight, contentDescription = "View Details")
            }
        }
    }

}




