package com.uniandes.ecobites.ui.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MenuItem::class], version = 1)
abstract class MenuDatabase : RoomDatabase() {
    abstract fun menuDao(): MenuDao
}