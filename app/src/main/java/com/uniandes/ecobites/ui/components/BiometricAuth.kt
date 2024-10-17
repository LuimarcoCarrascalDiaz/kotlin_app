package com.uniandes.ecobites.ui.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal
import androidx.core.app.ActivityCompat

class BiometricAuth(private val context: Context) {

    private var cancellationSignal: CancellationSignal? = null

    private val authenticationCallback: FingerprintManager.AuthenticationCallback =
        object : FingerprintManager.AuthenticationCallback() {
            @Deprecated("Deprecated in Java")
            override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                // Aquí manejas el éxito de la autenticación
            }

            @Deprecated("Deprecated in Java")
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Aquí manejas la autenticación fallida
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

    // Función para iniciar la autenticación por huella
    fun authenticate() {
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
