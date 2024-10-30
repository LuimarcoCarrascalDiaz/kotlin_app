package com.uniandes.ecobites.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uniandes.ecobites.ui.data.MenuDao
import com.uniandes.ecobites.ui.data.MenuDatabase
import com.uniandes.ecobites.ui.data.MenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import com.uniandes.ecobites.R

// ViewModel que utiliza LiveData en lugar de Flow
class MenuViewModel(database: MenuDatabase) : ViewModel() {
    private val menuDao: MenuDao = database.menuDao()

    // LiveData para observar los elementos del menú
    private val _menuItems = MediatorLiveData<List<MenuItem>>()
    val menuItems: LiveData<List<MenuItem>> = _menuItems

    init {
        loadMenuItems()
    }

    private fun loadMenuItems()  {
        _menuItems.addSource(menuDao.getAllMenuItems()) { items ->
            _menuItems.value = items
        }
    }

    fun addMenuItem(dishName: String, price: Double) {
        val newMenuItem = MenuItem(id = UUID.randomUUID().toString(), name = dishName, price = price)
        viewModelScope.launch(Dispatchers.IO) {
            menuDao.saveMenuItem(newMenuItem)
            loadMenuItems() // Recargar los datos después de añadir un elemento
        }
    }

    fun deleteMenuItem(menuItem: MenuItem) {
        viewModelScope.launch(Dispatchers.IO) {
            menuDao.deleteMenuItem(menuItem)
            loadMenuItems() // Recargar los datos después de eliminar un elemento
        }
    }
}

// Pantalla de Storage que utiliza el MenuViewModel con LiveData
@Composable
fun StorageScreen(menuDatabase: MenuDatabase, menuViewModel: MenuViewModel = viewModel(factory = MenuViewModelFactory(menuDatabase))) {
    var dishName by remember { mutableStateOf("") }
    var priceInput by remember { mutableStateOf("") }
    val menuItems by menuViewModel.menuItems.observeAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.padding(16.dp)) {
            TextField(
                modifier = Modifier.weight(0.6f),
                value = dishName,
                onValueChange = { dishName = it },
                label = { Text("Dish name") }
            )
            Spacer(modifier = Modifier.width(16.dp))
            TextField(
                modifier = Modifier.weight(0.4f),
                value = priceInput,
                onValueChange = { priceInput = it },
                label = { Text("Price") }
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = {
                val price = priceInput.toDoubleOrNull()
                if (price != null) {
                    menuViewModel.addMenuItem(dishName, price)
                    dishName = ""
                    priceInput = ""
                }
            }
        ) {
            Text("Add dish")
        }

        ItemsList(menuItems, menuViewModel)
    }
}

// Composable para mostrar la lista de elementos del menú
@Composable
fun ItemsList(menuItems: List<MenuItem>, menuViewModel: MenuViewModel) {
    if (menuItems.isEmpty()) {
        Text(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            text = "The menu is empty"
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            items(items = menuItems) { menuItem ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(menuItem.name)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        textAlign = TextAlign.Right,
                        text = "%.2f".format(menuItem.price)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { menuViewModel.deleteMenuItem(menuItem) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.delete_icon),
                            contentDescription = "Delete"
                        )
                    }
                }
            }
        }
    }
}

// Factory para crear el MenuViewModel con una instancia de MenuDatabase
class MenuViewModelFactory(private val database: MenuDatabase) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
