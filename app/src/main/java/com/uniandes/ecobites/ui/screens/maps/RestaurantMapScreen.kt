import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch

@Composable
fun RestaurantMapScreen() {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var predictions by remember { mutableStateOf(listOf<Pair<String, String>>()) } // List of Pair (placeId, description)
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Inicializar Google Places API
    LaunchedEffect(Unit) {
        Places.initialize(context.applicationContext, "AIzaSyCTHkA6PG3Zr_nhxq8N7dlX-vrmEM4mltY")
        val placesClient = Places.createClient(context)

        // Verificar permisos de ubicación
        permissionGranted = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    Column {
        // Barra de búsqueda
        TextField(
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
                coroutineScope.launch {
                    if (newText.text.isNotBlank()) {
                        isLoading = true
                        val placesClient = Places.createClient(context)
                        val request = FindAutocompletePredictionsRequest.builder()
                            .setQuery(newText.text)
                            .build()
                        placesClient.findAutocompletePredictions(request)
                            .addOnSuccessListener { response ->
                                predictions = response.autocompletePredictions.map {
                                    it.placeId to it.getFullText(null).toString() // Save placeId and description
                                }
                                isLoading = false
                            }
                            .addOnFailureListener {
                                isLoading = false
                                Toast.makeText(context, "Error al buscar lugares", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        predictions = emptyList()
                    }
                }
            },
            placeholder = { Text("Buscar lugar...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            }
        )

        // Mostrar sugerencias basadas en autocompletar
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(predictions) { (placeId, description) ->
                Text(
                    text = description,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            // Obtener detalles del lugar seleccionado
                            coroutineScope.launch {
                                val placesClient = Places.createClient(context)
                                val placeFields = listOf(Place.Field.LAT_LNG, Place.Field.NAME)
                                val fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build()

                                placesClient.fetchPlace(fetchPlaceRequest)
                                    .addOnSuccessListener { fetchPlaceResponse ->
                                        val place = fetchPlaceResponse.place
                                        selectedLocation = place.latLng
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Error al obtener detalles del lugar", Toast.LENGTH_LONG).show()
                                    }
                            }
                        }
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        if (permissionGranted) {
            // Coordenadas iniciales de Bogotá
            val bogota = LatLng(4.60971, -74.08175)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(bogota, 15f)
            }

            GoogleMap(
                modifier = Modifier.weight(1f),
                cameraPositionState = cameraPositionState,
            ) {
                // Si hay una ubicación seleccionada, mostrar el marcador
                selectedLocation?.let { location ->
                    Marker(
                        state = rememberMarkerState(position = location),
                        title = "Lugar seleccionado"
                    )
                }
            }
        } else {
            Text("Se requieren permisos de ubicación para mostrar el mapa.")
        }
    }
}
