package com.uniandes.ecobites.ui.screens.restaurants

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

class HornitosViewModel : ViewModel() {


    private val firestore = FirebaseFirestore.getInstance() // Inicializa Firestore

    private val _menuItems = MutableStateFlow<List<String>>(emptyList())
    val menuItems: StateFlow<List<String>> get() = _menuItems

    private val _firebaseProduct = MutableStateFlow<String?>(null)
    val firebaseProduct: StateFlow<String?> get() = _firebaseProduct



    fun fetchAllProducts(restaurantId: String, context: Context) {
        viewModelScope.launch {
            firestore.collection("restaurantes")
                .document(restaurantId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val products = document.data?.filterKeys { it.startsWith("producto") }
                            ?.values
                            ?.map { it.toString() } ?: emptyList()

                        // Actualiza el StateFlow con los productos obtenidos de Firestore
                        _menuItems.value = products

                        // Guarda los productos en SharedPreferences
                        saveProductsLocally(context, products)
                    } else {
                        println("El documento no existe.")
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error al obtener los productos: ${exception.message}")
                }
        }
    }
    // Guarda la lista de productos en SharedPreferences
    private fun saveProductsLocally(context: Context, products: List<String>) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("ProductPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("products", products.toSet())
        editor.apply()
    }

    // Carga los productos desde SharedPreferences
    fun loadProductsFromLocal(context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("ProductPrefs", Context.MODE_PRIVATE)
        val products = sharedPreferences.getStringSet("products", emptySet())?.toList() ?: emptyList()
        _menuItems.value = products
    }
}

@Composable
fun HornitosScreen(
    viewModel: HornitosViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    context: Context
) {
    val items = viewModel.menuItems.collectAsState()

    LaunchedEffect(Unit) {
        val isConnected = isNetworkAvailable(context)

        if (isConnected) {
            viewModel.fetchAllProducts("ENaEJSBKgF6jcVigzCtu", context)
            Toast.makeText(context, "Loaded remotely", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.loadProductsFromLocal(context)
            Toast.makeText(context, "Loaded locally", Toast.LENGTH_SHORT).show()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            MenuItems(items.value) // Muestra la lista de productos
        }
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
@Composable
fun MenuItems(items: List<String>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        itemsIndexed(items) { _, item ->
            MenuItemDetails(item)
        }
    }
}

@Composable
fun MenuItemDetails(menuItem: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = menuItem,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
