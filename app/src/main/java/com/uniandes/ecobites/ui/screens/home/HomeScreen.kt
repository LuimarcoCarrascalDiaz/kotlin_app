package com.uniandes.ecobites.ui.screens.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.uniandes.ecobites.R
import RestaurantViewModel
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

// Datos de las tiendas y categorías
data class Store(val name: String, val imageResId: Int)
data class Category(val name: String, val icon: ImageVector)

val categories = listOf(
    Category("Restaurant", Icons.Rounded.Home),
    Category("Ingredients", Icons.Rounded.Edit),
    Category("Store", Icons.Rounded.ShoppingCart),
    Category("Diet", Icons.Rounded.Check)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val restaurantViewModel: RestaurantViewModel = viewModel()
    val stores by restaurantViewModel.restaurants.collectAsState()
    val isLoading by restaurantViewModel.isLoading.collectAsState()

    val carouselViewModel: CarouselViewModel = viewModel()
    val carouselImages by carouselViewModel.carouselImages.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {

            restaurantViewModel.loadStores()
            carouselViewModel.fetchCarouselImages()

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopSection()
        OfferCarousel()
        CategoriesRow()

        if (isLoading ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            StoresGrid(
                navController = navController,
                restaurants = stores,
                imageUrls = carouselImages,
                context = context

            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSection() {
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
        ) {
            Icon(Icons.Outlined.LocationOn, contentDescription = "Location", tint = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Calle 13 #10-22", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search deals...") },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search Icon") },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(Icons.Outlined.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp, max = 72.dp)
                .padding(horizontal = 8.dp)
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun OfferCarousel() {
    val items = listOf(
        R.drawable.percent,
        R.drawable.free_delivery,
        R.drawable.two_for_one,
        R.drawable.fiftyoff
    )

    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { items.count() },
        modifier = Modifier
            .height(250.dp)
            .fillMaxWidth(),
        preferredItemWidth = 250.dp,
        itemSpacing = 8.dp
    ) { index ->
        Image(
            painter = painterResource(id = items[index]),
            contentDescription = "Carousel Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(300.dp)
                .clip(MaterialTheme.shapes.extraLarge)
        )
    }
}

@Composable
fun CategoriesRow() {
    var selectedTabIndex by remember { mutableStateOf(0) }

    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        edgePadding = 16.dp
    ) {
        categories.forEachIndexed { index, category ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { selectedTabIndex = index },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = "${category.name} Icon",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = category.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun StoresGrid(
    navController: NavController,
    restaurants: List<Store>,
    imageUrls: List<String>,
    context: Context
) {
    val hasInternet = remember { isNetworkAvailable(context) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
    ) {
        items(restaurants) { store ->
            StoreItem(
                store = store,
                onClick = {
                    navController.navigate("hornitos")
                }
            )
        }

        items(imageUrls) { imageUrl ->
            FirebaseImageItem(imageUrl = imageUrl, context = context, hasInternet = hasInternet)
        }
    }
}

@Composable
fun StoreItem(store: Store, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.clickable { onClick() }
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

@Composable
fun FirebaseImageItem(imageUrl: String, context: Context, hasInternet: Boolean) {
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .diskCachePolicy(CachePolicy.ENABLED) // Siempre usa caché de disco
        .memoryCachePolicy(CachePolicy.ENABLED) // Siempre usa caché de memoria
        .networkCachePolicy(if (hasInternet) CachePolicy.ENABLED else CachePolicy.DISABLED)
        .listener(
            onStart = {
                Log.d("FirebaseImageItem", "Attempting to load image: $imageUrl")
            },
            onSuccess = { request, result ->
                Log.d("FirebaseImageItem", "Image loaded successfully: $imageUrl")
                Toast.makeText(context, "Image loaded successfully from ${result.dataSource}", Toast.LENGTH_SHORT).show()
            },
            onError = { request, result ->
                Log.e("FirebaseImageItem", "Error loading image from cache or network: $imageUrl")
                Toast.makeText(context, "Failed to load image from cache: $imageUrl", Toast.LENGTH_SHORT).show()
            }
        )
        .build()

    AsyncImage(
        model = request,
        contentDescription = "Imagen promocional",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(MaterialTheme.shapes.extraLarge)
    )
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
