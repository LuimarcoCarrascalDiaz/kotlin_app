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

    fun fetchProductFromFirestore(restaurantId: String) {
        viewModelScope.launch {
            firestore.collection("restaurantes")
                .document(restaurantId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val producto = document.getString("producto")
                        println("Producto obtenido desde Firestore: $producto") // LOG
                        _firebaseProduct.value = producto
                    } else {
                        println("El documento no existe en Firestore") // LOG
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error al obtener el producto: ${exception.message}") // LOG
                }
        }
    }
}

@Composable
fun HornitosScreen(viewModel: HornitosViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val items = viewModel.menuItems.collectAsState()
    val firebaseProduct = viewModel.firebaseProduct.collectAsState()

    LaunchedEffect(Unit) {
        // Llama a la API para obtener el menú
        viewModel.fetchMenu("Salads")
        // Llama a Firestore para obtener el producto agregado
        viewModel.fetchProductFromFirestore("ENaEJSBKgF6jcVigzCtu")
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
            MenuItems(items.value)

            // Mostrar producto desde Firestore si está disponible
            firebaseProduct.value?.let { product ->
                println("Producto mostrado en la UI: $product") // LOG

                Text(
                    text = "Producto desde Firestore:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = product,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
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
