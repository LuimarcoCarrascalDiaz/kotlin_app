package com.uniandes.ecobites.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uniandes.ecobites.R
import com.uniandes.ecobites.ui.data.CartProduct
import com.uniandes.ecobites.ui.data.clearCart
import com.uniandes.ecobites.ui.data.fetchCartItemsWithDetails
import com.uniandes.ecobites.ui.data.removeFromCart
import com.uniandes.ecobites.ui.data.updateCartItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(userId: String) {
    val scope = rememberCoroutineScope()
    var cartItems by remember { mutableStateOf<List<CartProduct>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch cart items when the screen loads
    LaunchedEffect(userId) {
        scope.launch {
            isLoading = true
            cartItems = fetchCartItemsWithDetails(userId)
            isLoading = false
        }
    }

    // Clear cart functionality
    fun handleClearCart() {
        scope.launch {
            clearCart(userId)
            cartItems = emptyList() // Clear the local state as well
        }
    }

    // Function to update quantity
    fun updateQuantity(cartItemId: Int, newQuantity: Int, productId: Int) {
        scope.launch {
            updateCartItem(productId, newQuantity, userId) // Update in the database

            if (newQuantity > 0) {
                cartItems = cartItems.map {
                    if (it.id == cartItemId) it.copy(quantity = newQuantity) else it
                }
            } else {
                // Remove the item if quantity goes to zero
                removeFromCart(cartItemId,userId)
                // Update the local state to remove the item
                cartItems = cartItems.filterNot { it.id == cartItemId }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cart") },
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
                OutlinedButton(
                    onClick = { handleClearCart() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Text(text = "Clear", color = Color.Red)
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Subtotal",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = calculateSubtotal(cartItems) + " COP",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Button(
                    onClick = { /* Handle checkout */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF907B00) // Adjust color as needed
                    )
                ) {
                    Text(text = "Checkout")
                }
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else if (cartItems.isEmpty()) {
            Text(
                text = "Your cart is empty",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cartItems) { cartItem ->
                    CartItemCard(
                        product = cartItem,
                        onIncreaseQuantity = { updateQuantity(cartItem.id, cartItem.quantity + 1,cartItem.products.id) },
                        onDecreaseQuantity = {  updateQuantity(cartItem.id, cartItem.quantity - 1,cartItem.products.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    product: CartProduct,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.def), // Replace with product image if available
                contentDescription = "Product Image",
                modifier = Modifier.size(100.dp).clip(MaterialTheme.shapes.medium)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.products.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = product.products.price.toString() + " COP",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDecreaseQuantity) {
                    Icon(Icons.Rounded.KeyboardArrowLeft, contentDescription = "Decrease quantity")
                }
                Text(text = product.quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = onIncreaseQuantity) {
                    Icon(Icons.Rounded.KeyboardArrowRight, contentDescription = "Increase quantity")
                }
            }
        }
    }
}


// This can stay here or be moved to a separate file for utility functions
fun calculateSubtotal(cartItems: List<CartProduct>): String {
    val subtotal = cartItems.sumOf { ( it.products.price) * it.quantity }
    return subtotal.toString()  // Format as currency without decimals
}
