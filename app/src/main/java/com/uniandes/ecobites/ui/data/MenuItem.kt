package com.uniandes.ecobites.ui.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MenuItem(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double
)
