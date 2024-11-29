package com.uniandes.ecobites.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarouselViewModel : ViewModel() {
    private val _carouselImages = MutableStateFlow<List<String>>(emptyList())
    val carouselImages: StateFlow<List<String>> get() = _carouselImages

    private val firebaseStorage = FirebaseStorage.getInstance()

    fun fetchCarouselImages() {
        viewModelScope.launch {
            val storageRef = firebaseStorage.reference.child("imagenesRestaurantes")
            storageRef.listAll()
                .addOnSuccessListener { result ->
                    val urls = mutableListOf<String>()
                    val tasks = result.items.map { it.downloadUrl }

                    tasks.forEach { task ->
                        task.addOnSuccessListener { uri ->
                            urls.add(uri.toString())
                            if (urls.size == result.items.size) {
                                _carouselImages.value = urls
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error al obtener imágenes: ${exception.message}")
                }
        }
    }
}
