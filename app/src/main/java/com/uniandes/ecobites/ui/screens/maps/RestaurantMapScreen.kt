import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.MapUiSettings  // Cambia por MapUiSettings, la referencia correcta

@Composable
fun RestaurantMapScreen() {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }

    // Verificar los permisos de ubicación cada vez que la pantalla se compone
    LaunchedEffect(Unit) {
        permissionGranted = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        Log.d("RestaurantMapScreen", "Permiso de ubicación concedido: $permissionGranted")
    }

    if (permissionGranted) {
        // Coordenadas de Bogotá
        val bogota = LatLng(4.60971, -74.08175)

        // Crear el estado de la posición de la cámara con zoom inicial
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(bogota, 15f)
        }

        // Configuración de la interfaz del mapa (Habilitar controles de zoom)
        val mapUiSettings = remember {
            MapUiSettings(zoomControlsEnabled = true)  // Configurar los controles de zoom
        }

        // Mover `rememberMarkerState` fuera de `onMapLoaded`
        val markerState = rememberMarkerState(position = bogota)

        Log.d("RestaurantMapScreen", "Configuración del mapa cargada correctamente")

        // Mostrar el mapa
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = mapUiSettings,  // Usar MapUiSettings correctamente
        ) {
            // Solo cuando el mapa esté cargado, agregamos el marcador
            Marker(
                state = markerState,  // Usar el marcador que fue creado en el contexto composable
                title = "Restaurante Ejemplo",
                snippet = "Descripción del restaurante"
            )
            Log.d("RestaurantMapScreen", "Marcador añadido correctamente")
        }
    } else {
        Log.d("RestaurantMapScreen", "Permisos de ubicación no concedidos")
        Text("Se requieren permisos de ubicación para mostrar el mapa.")
    }
}

