import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
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
    var places by remember { mutableStateOf(listOf<Place>()) }
    var predictions by remember { mutableStateOf(listOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Inicializar Google Places API con tu clave
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
                    isLoading = true
                    val request = FindAutocompletePredictionsRequest.builder()
                        .setQuery(newText.text)
                        .build()
                    val placesClient = Places.createClient(context)
                    placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener { response ->
                            predictions = response.autocompletePredictions.map { it.getFullText(null).toString() }
                            isLoading = false
                        }
                        .addOnFailureListener {
                            isLoading = false
                            Toast.makeText(context, "Error al buscar lugares", Toast.LENGTH_LONG).show()
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
            items(predictions) { prediction ->
                Text(text = prediction, modifier = Modifier.padding(8.dp))
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        if (permissionGranted) {
            // Coordenadas de Bogotá
            val bogota = LatLng(4.60971, -74.08175)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(bogota, 15f)
            }

            GoogleMap(
                modifier = Modifier.weight(1f),
                cameraPositionState = cameraPositionState,
            ) {
                places.forEach { place ->
                    place.latLng?.let { latLng ->
                        Marker(
                            state = rememberMarkerState(position = latLng),
                            title = place.name
                        )
                    }
                }
            }
        } else {
            Text("Se requieren permisos de ubicación para mostrar el mapa.")
        }
    }
}

