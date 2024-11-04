package com.uniandes.ecobites.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.uniandes.ecobites.R

@Composable
fun ProfileScreen(navController: NavController) {
    var name by remember { mutableStateOf("Pepito Andrés") }
    var surname by remember { mutableStateOf("Rincon Arismendy") }
    var citizenId by remember { mutableStateOf("1005634120") }
    var email by remember { mutableStateOf("p.rincon@uniandes.edu.co") }
    var phone by remember { mutableStateOf("1234567890") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Habilitar scroll vertical
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¡Hi, Pepito!",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "You can edit your personal information here.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Imagen de perfil
        Image(
            painter = painterResource(id = R.drawable.profile_image),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        )

        Spacer(modifier = Modifier.height(32.dp))

        ProfileTextField(label = "Name", value = name, onValueChange = { name = it })
        ProfileTextField(label = "Surname", value = surname, onValueChange = { surname = it })
        ProfileTextField(label = "Citizen ID", value = citizenId, onValueChange = { citizenId = it })
        ProfileTextField(label = "Email", value = email, onValueChange = { email = it })
        ProfileTextField(label = "Phone", value = phone, onValueChange = { phone = it })

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Caching
        Button(
            onClick = { navController.navigate("caching") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Caching")
        }
    }
}

@Composable
fun ProfileTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Adds space between text fields
        singleLine = true
    )
}
