plugins {
    id("com.android.application") version "8.6.1" // AGP version
    id("org.jetbrains.kotlin.android") version "2.0.20" // Kotlin plugin for Android
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20" // Kotlin serialization plugin
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20"
    id ("kotlin-kapt") // Asegúrate de que kapt esté habilitado
}

android {
    namespace = "com.uniandes.ecobites"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.uniandes.ecobites"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {


    // Supabase dependencies
    implementation(platform("io.github.jan-tennert.supabase:bom:3.0.0"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.ktor:ktor-client-android:3.0.0-rc-1")

    // AndroidX dependencies
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.2")

    // Compose dependencies
    implementation(platform("androidx.compose:compose-bom:2024.09.03"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.navigation:navigation-runtime-ktx:2.8.2")
    implementation("androidx.navigation:navigation-compose:2.8.2")
    implementation ("androidx.biometric:biometric:1.1.0")
    implementation("androidx.fragment:fragment-ktx:1.8.4")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.libraries.places:places:4.0.0")
    implementation ("com.google.accompanist:accompanist-permissions:0.28.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation ("com.google.maps.android:maps-compose:2.2.0")
    implementation("androidx.room:room-common:2.6.1")
    implementation("com.google.accompanist:accompanist-flowlayout:0.28.0") // O la última versión disponible
    implementation ("androidx.room:room-runtime:2.4.3")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.4")
    kapt ("androidx.room:room-compiler:2.5.1") // Procesador de anotaciones para Room
    implementation ("com.github.bumptech.glide:glide:4.12.") // Glide estándar
    implementation ("com.github.bumptech.glide:compose:1.0.0-alpha.1") // Glide para Jetpack Compose
    kapt ("com.github.bumptech.glide:compiler:4.12.0") // Para anotar métodos de Glide
    implementation("io.coil-kt:coil-compose:2.0.0")
    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.03"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("io.mockk:mockk:1.13.3") // revisa la última versión
    // Debug dependencies
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

}