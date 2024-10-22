package com.uniandes.ecobites.ui.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController

class BiometricAuth(private val context: Context) {

    private var cancellationSignal: CancellationSignal? = null

    // agregamos el navController para la navegación
    private var navController: NavController? = null


    private val authenticationCallback: FingerprintManager.AuthenticationCallback =
        object : FingerprintManager.AuthenticationCallback() {
            @Deprecated("Deprecated in Java")
            override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                // Aquí manejo el éxito de la autenticación
                navController?.navigate("home") {
                    popUpTo("login") { inclusive = true}
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Aquí manejo la autenticación fallida
            }
        }

    // Función para verificar si la autenticación por huella está soportada en el dispositivo
    fun isFingerprintSupported(): Boolean {
        val fingerprintManager = context.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager

        return if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            false
        } else {
            fingerprintManager.isHardwareDetected && fingerprintManager.hasEnrolledFingerprints()
        }
    }

    // Función para iniciar la autenticación por huella y navegar a la pantalla de home si la navegación es exitosa
    fun authenticate(navController: NavController) {
        this.navController = navController // guardamos el navController para poder navegar después
        val fingerprintManager = context.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.USE_FINGERPRINT,Manifest.permission.USE_BIOMETRIC),
                101
            )// Permisos de huella no están concedidos, manejo de caso
            return
        }

        cancellationSignal = CancellationSignal()

        fingerprintManager.authenticate(null, cancellationSignal, 0, authenticationCallback, null)
    }
}
