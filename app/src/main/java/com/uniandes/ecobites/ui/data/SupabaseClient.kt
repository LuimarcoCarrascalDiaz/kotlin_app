package com.uniandes.ecobites.ui.data

import android.util.Log
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.SupabaseClientBuilder
import io.github.jan.supabase.createSupabaseClient

// Create Supabase Client
val supabase = createSupabaseClient(
    supabaseUrl = "https://nlhcaanwwchxdzdiyizf.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5saGNhYW53d2NoeGR6ZGl5aXpmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjc5MDc0OTQsImV4cCI6MjA0MzQ4MzQ5NH0.LrcRGkVH1qjPE09xDngX7wrtrUmfIYbTGrgbPKarTeM"
) {
    install(Postgrest)
    install(Auth)
}
@Serializable
data class Restaurant(
    val id: Int,
    val name: String,
    val description: String?,
    val address: String?,
    val phone: String?
)
// Store model
@Serializable
data class Store(
    val id: Int,
    val name: String,
    val description: String?,
    val address: String?,
    val phone: String?
)

// Fetch store details based on storeName
suspend fun fetchStore(storeName: String): Store {
    return withContext(Dispatchers.IO) {
        supabase.from("stores").select() {
            filter {
                eq("name", storeName)
            }
        }.decodeSingle<Store>()
    }
}

@Serializable
data class Product(
    val id: Int,
    val store_id: Int,
    val name: String,
    val price: Int
)

// Fetch store products based on storeId
suspend fun fetchStoreProducts(storeId: Int): List<Product> {
    return withContext(Dispatchers.IO) {
        supabase.from("products").select() {
            filter {
                eq("store_id", storeId)
            }
        }.decodeList<Product>()
    }
}

// Concurrently fetch store and its products
suspend fun fetchStoreAndProducts(storeName: String): Pair<Store, List<Product>> = coroutineScope {
    // Launch both fetches concurrently
    val storeDeferred = async { fetchStore(storeName) }
    val productsDeferred = async {
        val store = storeDeferred.await() // Wait for store ID to fetch products
        fetchStoreProducts(store.id)
    }

    // Await and return both results as a Pair
    val store = storeDeferred.await()
    val products = productsDeferred.await()

    Pair(store, products)
}

// Function to handle user sign-in
suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
    return withContext(Dispatchers.IO) {
        try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

suspend fun signUpWithEmailAndName(email: String, password: String, name: String): Result<Unit> {
    return withContext(Dispatchers.IO) {
        try {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("display_name", name)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Serializable
data class CartItem(
    val id: Int? = null,
    val user_id: String,
    val product_id: Int,
    val quantity: Int
)

suspend fun clearCart(userId: String) {
    supabase.from("cart_items").delete {
        filter { eq("user_id", userId) }
    }
}

suspend fun addToCart(product: Product, quantity: Int, userId: String) {
    val cartItem = CartItem(
        user_id = userId,
        product_id = product.id,
        quantity = quantity
    )
    supabase.from("cart_items").insert(cartItem)
}

suspend fun updateCartItem(productId: Int, newQuantity: Int, userId: String) {
    if (newQuantity > 0) {
        supabase.from("cart_items").update(
            mapOf("quantity" to newQuantity)
        ) {
            filter {
                eq("product_id", productId)
                eq("user_id", userId)
            }
        }
    } else {
        supabase.from("cart_items").delete {
            filter {
                eq("product_id", productId)
                eq("user_id", userId)
            }
        }
    }
}

suspend fun removeFromCart(productId: Int, userId: String) {
    supabase.from("cart_items").delete {
        filter {
            eq("product_id", productId)
            eq("user_id", userId)
        }
    }
}


// Creación del cliente Supabase sin autenticación para pruebas
fun createSupabaseClientForTesting(): SupabaseClient {
    return createSupabaseClient(
        supabaseUrl = "https://your-supabase-url.supabase.co",
        supabaseKey = "your-anon-key"
    ) {
        // Aquí podrías agregar otras configuraciones de prueba si son soportadas por la biblioteca
        // Esto es un cliente básico, sin manejo de sesión.
    }
}
@Serializable
data class CartProduct(
    val id: Int,
    val quantity: Int,
    val products: ProductInCart
)

@Serializable
data class ProductInCart(
    val id: Int,
    val name: String,
    val price: Int
)

suspend fun fetchCartItemsWithDetails(userId: String): List<CartProduct> {
    val rawResponse = supabase.from("cart_items")
        .select(Columns.raw("id, quantity, products(id, name, price)")) {
            filter { eq("user_id", userId) }
        }

    Log.d("SupabaseResponse", "Raw JSON Response: ${rawResponse.data}")

    return rawResponse.decodeList<CartProduct>()
}
