plugins {
    id("com.android.application") version "8.6.1" apply false // AGP version
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false // Kotlin plugin for Android
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.6.1")
        classpath("com.google.gms:google-services:4.3.15")
    }
}