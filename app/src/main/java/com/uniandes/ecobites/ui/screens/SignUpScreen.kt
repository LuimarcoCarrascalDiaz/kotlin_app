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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.uniandes.ecobites.R
import com.uniandes.ecobites.ui.data.signUpWithEmailAndName  // Import the sign-up function
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(onSignUpSuccess: () -> Unit,navController: NavController) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }  // Name input state
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        // Sign-Up text at the top
        Text(
            text = "Sign Up",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 28.dp)
        )

        // Content in the center
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

            // Name Input Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email Input Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
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

            // Sign-Up Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        val result = signUpWithEmailAndName(email, password, name)  // Call the sign-up function
                        result.onSuccess {
                            Toast.makeText(context, "Signed up successfully!", Toast.LENGTH_LONG).show()
                            onSignUpSuccess()  // Trigger the success callback
                        }.onFailure { e ->
                            Toast.makeText(context, "Sign up failed: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(48.dp)
            ) {
                Text("Sign Up")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Navigate to Login Button (optional)
            TextButton(
                onClick = {
                    navController.navigate("login")
                }
            ) {
                Text("Already have an account? Log In")
            }
        }
    }
}

