package com.uniandes.ecobites.ui.screens.store

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uniandes.ecobites.ui.data.Product
import com.uniandes.ecobites.ui.data.Store
import com.uniandes.ecobites.ui.data.fetchStore
import com.uniandes.ecobites.ui.data.fetchStoreProducts
import com.uniandes.ecobites.ui.data.addToCart
import com.uniandes.ecobites.ui.data.removeFromCart
import com.uniandes.ecobites.ui.data.updateCartItem
import kotlinx.coroutines.launch

@Composable
fun StoreDetailsScreen(storeName: String, userId: String) {  // Pass the userId to the screen
    var store by remember { mutableStateOf<Store?>(null) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val cartState = remember { mutableStateMapOf<Int, Int>() }

    LaunchedEffect(storeName) {
        store = fetchStore(storeName)
        store?.let {
            products = fetchStoreProducts(it.id)
        }
    }

    if (store == null) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(text = "Store: ${store!!.name}", style = MaterialTheme.typography.bodyMedium)
            Text(text = store!!.description ?: "No description available")

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Products", style = MaterialTheme.typography.bodySmall)

            LazyColumn {
                items(products) { product ->
                    ProductRow(
                        product = product,
                        quantity = cartState[product.id] ?: 0,
                        onAddToCart = {
                            // Add to cart action
                            scope.launch {
                                addToCart(product, 1, userId)  // Pass the userId
                                cartState[product.id] = 1
                            }
                        },
                        onIncreaseQuantity = {
                            // Increase quantity
                            val newQuantity = (cartState[product.id] ?: 0) + 1
                            scope.launch {
                                updateCartItem(product.id, newQuantity, userId)  // Pass the userId
                                cartState[product.id] = newQuantity
                            }
                        },
                        onDecreaseQuantity = {
                            // Decrease quantity
                            val newQuantity = (cartState[product.id] ?: 0) - 1
                            if (newQuantity > 0) {
                                scope.launch {
                                    updateCartItem(product.id, newQuantity, userId)  // Pass the userId
                                    cartState[product.id] = newQuantity
                                }
                            } else {
                                scope.launch {
                                    removeFromCart(product.id, userId)  // Pass the userId
                                    cartState.remove(product.id)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductRow(
    product: Product,
    quantity: Int,
    onAddToCart: () -> Unit,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = product.name)
            Text(text = "$${product.price}")
        }
        if (quantity > 0) {
            Row {
                IconButton(onClick = onDecreaseQuantity) {
                    Icon(imageVector = Icons.Rounded.KeyboardArrowLeft, contentDescription = "Decrease quantity")
                }
                Text(text = quantity.toString(), modifier = Modifier.alignByBaseline())
                IconButton(onClick = onIncreaseQuantity) {
                    Icon(imageVector = Icons.Rounded.KeyboardArrowRight, contentDescription = "Increase quantity")
                }
            }
        } else {
            Button(onClick = onAddToCart) {
                Text(text = "Add to Cart")
            }
        }
    }
}
