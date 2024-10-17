import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.uniandes.ecobites.R

// Data class representing a store
data class Store(val name: String, val imageResId: Int)

// List of stores with their images
val stores = listOf(
    Store("Exito", R.drawable.exito),
    Store("Hornitos", R.drawable.hornitos),
    Store("McDonalds", R.drawable.mc_donalds),
    Store("Dunkin Donuts", R.drawable.dunkin_donuts),
    Store("Pan Pa Ya!", R.drawable.pan_pa_ya)
)

@Composable
fun StoresGrid(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            stores.take(2).forEach { store ->
                StoreItem(
                    store = store,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate("store/${store.name}")  // Navigate to store details
                    }
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            stores.drop(2).take(3).forEach { store ->
                StoreItem(
                    store = store,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate("store/${store.name}")  // Navigate to store details
                    }
                )
            }
        }
    }
}

@Composable
fun StoreItem(store: Store, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier.clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = store.imageResId),
            contentDescription = store.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(MaterialTheme.shapes.extraLarge)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = store.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
