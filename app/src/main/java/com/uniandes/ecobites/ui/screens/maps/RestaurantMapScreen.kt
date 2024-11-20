import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
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
import com.uniandes.ecobites.R
import kotlinx.coroutines.launch

@Composable
fun RestaurantMapScreen() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var permissionGranted by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var predictions by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Inicializar Google Places API
    LaunchedEffect(Unit) {
        if (!isNetworkAvailable(context)) {
            Toast.makeText(context, "No hay conexión, intente más tarde", Toast.LENGTH_SHORT).show()
        } else {
            Places.initialize(context.applicationContext, "AIzaSyCTHkA6PG3Zr_nhxq8N7dlX-vrmEM4mltY")
            val placesClient = Places.createClient(context)

            // Verificar permisos de ubicación
            permissionGranted = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            // Obtener ubicación actual si los permisos están concedidos
            if (permissionGranted) {
                getUserLocation(context, fusedLocationClient) { location ->
                    selectedLocation = LatLng(location.latitude, location.longitude)
                }
            } else {
                Toast.makeText(context, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLocation ?: LatLng(4.60971, -74.08175), 15f)
    }

    Column {
        // Barra de búsqueda
        TextField(
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
                coroutineScope.launch {
                    if (!isNetworkAvailable(context)) {
                        Toast.makeText(context, "No hay conexión, intente más tarde", Toast.LENGTH_SHORT).show()
                    } else if (newText.text.isNotBlank()) {
                        isLoading = true
                        val placesClient = Places.createClient(context)
                        val request = FindAutocompletePredictionsRequest.builder()
                            .setQuery(newText.text)
                            .build()
                        placesClient.findAutocompletePredictions(request)
                            .addOnSuccessListener { response ->
                                predictions = response.autocompletePredictions.map {
                                    it.placeId to it.getFullText(null).toString()
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
                            coroutineScope.launch {
                                if (!isNetworkAvailable(context)) {
                                    Toast.makeText(context, "No hay conexión, intente más tarde", Toast.LENGTH_SHORT).show()
                                } else {
                                    val placesClient = Places.createClient(context)
                                    val placeFields = listOf(Place.Field.LAT_LNG, Place.Field.NAME)
                                    val fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build()

                                    placesClient.fetchPlace(fetchPlaceRequest)
                                        .addOnSuccessListener { fetchPlaceResponse ->
                                            val place = fetchPlaceResponse.place
                                            selectedLocation = place.latLng
                                            place.latLng?.let { location ->
                                                cameraPositionState.move(
                                                    CameraUpdateFactory.newLatLngZoom(location, 15f)
                                                )
                                            }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Error al obtener detalles del lugar", Toast.LENGTH_LONG).show()
                                        }
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
            GoogleMap(
                modifier = Modifier.weight(1f),
                cameraPositionState = cameraPositionState,
            ) {
                selectedLocation?.let { location ->
                    Marker(
                        state = rememberMarkerState(position = location),
                        title = "Mi ubicación"
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.wifiicon),
                    contentDescription = "Ícono de ubicación requerida",
                    modifier = Modifier.size(150.dp) // Ajusta el tamaño del ícono según tus necesidades
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Se requieren permisos de ubicación para mostrar el mapa.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

// Función para verificar si hay conexión de red
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

// Función para obtener la ubicación del usuario con verificación de permisos
fun getUserLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationResult: (location: Location) -> Unit
) {
    // Verificar si el permiso de ubicación está concedido
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                onLocationResult(it)
            } ?: Toast.makeText(context, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Error al obtener la ubicación", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show()
    }
}
