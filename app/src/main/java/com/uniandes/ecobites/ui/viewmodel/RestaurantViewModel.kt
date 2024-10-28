import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.ecobites.R
import com.uniandes.ecobites.ui.components.Store
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantViewModel : ViewModel() {

    // Lista de restaurantes que será observada por la UI
    val restaurants = mutableStateOf<List<Store>>(emptyList())

    // Estado de carga para mostrar un indicador de progreso
    val isLoading = mutableStateOf(false)

    // Función para cargar los restaurantes en segundo plano
    fun loadStores() {
        isLoading.value = true  // Inicia el estado de carga

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                // Aquí iría la lógica para obtener los datos (por ejemplo, desde una API o base de datos)
                loadStoresFromSource()  // Función que simula la obtención de datos
            }

            // Actualiza la lista de restaurantes y detiene el estado de carga en el hilo principal
            restaurants.value = result
            isLoading.value = false
        }
    }

    // Función que simula la obtención de datos (aquí iría una llamada real a la base de datos o API)
    private suspend fun loadStoresFromSource(): List<Store> {
        delay(2000)

        return listOf(
            Store("Exito", R.drawable.exito),
            Store("Hornitos", R.drawable.hornitos),
            Store("McDonalds", R.drawable.mc_donalds),
            Store("Dunkin Donuts", R.drawable.dunkin_donuts),
            Store("Pan Pa Ya!", R.drawable.pan_pa_ya)
        )
    }

}
