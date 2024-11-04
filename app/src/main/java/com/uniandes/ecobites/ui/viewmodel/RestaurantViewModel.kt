import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.ecobites.R

import com.uniandes.ecobites.ui.screens.home.Store
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantViewModel : ViewModel() {

    // StateFlow para la lista de restaurantes, que será observada por la UI
    private val _restaurants = MutableStateFlow<List<Store>>(emptyList())
    val restaurants: StateFlow<List<Store>> = _restaurants

    // StateFlow para el estado de carga, que también será observado por la UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Función para cargar los restaurantes en segundo plano
    fun loadStores() {
        _isLoading.value = true  // Inicia el estado de carga

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                // Aquí iría la lógica para obtener los datos (por ejemplo, desde una API o base de datos)
                loadStoresFromSource()  // Función que simula la obtención de datos
            }

            // Actualiza la lista de restaurantes y detiene el estado de carga en el hilo principal
            _restaurants.value = result
            _isLoading.value = false
        }
    }

    // Función que simula la obtención de datos (aquí iría una llamada real a la base de datos o API)
    private suspend fun loadStoresFromSource(): List<Store> {
        delay(2000)  // Simula un retraso en la obtención de datos

        return listOf(
            Store("Exito", R.drawable.exito),
            Store("Hornitos", R.drawable.hornitos),
            Store("McDonalds", R.drawable.mc_donalds),
            Store("Dunkin Donuts", R.drawable.dunkin_donuts),
            Store("Pan Pa Ya!", R.drawable.pan_pa_ya)
        )
    }
}
