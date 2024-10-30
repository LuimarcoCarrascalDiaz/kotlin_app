package com.uniandes.ecobites.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.uniandes.ecobites.R
import com.uniandes.ecobites.ui.data.signInWithEmail
import com.uniandes.ecobites.ui.components.BiometricAuth
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, navController: NavController, biometricAuth: BiometricAuth) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Verificar si la autenticación biométrica está soportada
    val isFingerprintSupported = biometricAuth.isFingerprintSupported()
    //Mostrar un Toast para verificar si la autenticación biométrica está soportada
    LaunchedEffect(Unit){

    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        // Log in text at the top
        Text(
            text = "Log in",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 28.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = "Eco Bites Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(350.dp)
                    .height(350.dp)
            )

            // Email Input Field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    if(it.length<=30) { // Limitar a 30 caracteres
                        email = it
                    }
                                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Password Input Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sign-In Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        val result = signInWithEmail(email, password)
                        result.onSuccess {
                            Toast.makeText(context, "Signed in successfully!", Toast.LENGTH_LONG).show()
                            onLoginSuccess()
                        }.onFailure { e ->
                            Toast.makeText(context, "Sign in failed: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(48.dp)
            ) {
                Text("Sign in")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Autenticación por huella si está soportada
            if (isFingerprintSupported) {
                Button(
                    onClick = {
                        biometricAuth.authenticate(navController)  // Inicia el proceso de autenticación biométrica
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(48.dp)
                ) {
                    Text("Iniciar sesión con huella")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sign-Up Button
            TextButton(
                onClick = {
                    navController.navigate("signup")  // Navigate to Sign-Up Screen
                }
            ) {
                Text("Don't have an account? Sign Up")
            }
        }
    }
}
