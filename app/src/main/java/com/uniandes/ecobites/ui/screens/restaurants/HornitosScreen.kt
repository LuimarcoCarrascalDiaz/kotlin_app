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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.ecobites.MenuCategory
import com.google.firebase.firestore.FirebaseFirestore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HornitosViewModel : ViewModel() {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(contentType = ContentType("text", "plain"))
        }
    }

    private val firestore = FirebaseFirestore.getInstance() // Inicializa Firestore

    private val _menuItems = MutableStateFlow<List<String>>(emptyList())
    val menuItems: StateFlow<List<String>> get() = _menuItems

    private val _firebaseProduct = MutableStateFlow<String?>(null)
    val firebaseProduct: StateFlow<String?> get() = _firebaseProduct

    fun fetchMenu(category: String) {
        viewModelScope.launch {
            val response: Map<String, MenuCategory> =
                client.get("https://raw.githubusercontent.com/Meta-Mobile-Developer-PC/Working-With-Data-API/main/littleLemonMenu.json")
                    .body()

            _menuItems.value = response[category]?.menu ?: emptyList()
        }
    }

    fun fetchAllProducts(restaurantId: String) {
        viewModelScope.launch {
            firestore.collection("restaurantes")
                .document(restaurantId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Filtra todos los campos que comiencen con "producto" y recupéralos como lista
                        val products = document.data?.filterKeys { it.startsWith("producto") }
                            ?.values
                            ?.map { it.toString() } ?: emptyList()

                        _menuItems.value = products // Actualiza el StateFlow con los productos
                    } else {
                        println("El documento no existe.")
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error al obtener los productos: ${exception.message}")
                }
        }
    }
    }

@Composable
fun HornitosScreen(viewModel: HornitosViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val items = viewModel.menuItems.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAllProducts("ENaEJSBKgF6jcVigzCtu") // Cargar todos los productos
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
