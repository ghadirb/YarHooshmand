plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.yarhooshmand"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.yarhooshmand"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "1.2"
    }

    val keystorePath: String = (project.findProperty("KEYSTORE_PATH") as String?) ?: "yarhooshmand.keystore"
    val keystorePassword: String = (project.findProperty("KEYSTORE_PASSWORD") as String?) ?: ""
    val keyAliasProp: String = (project.findProperty("KEY_ALIAS") as String?) ?: ""
    val keyPassword: String = (project.findProperty("KEY_PASSWORD") as String?) ?: ""

    signingConfigs {
        create("release") {
            storeFile = file(keystorePath)
            storePassword = keystorePassword
            keyAlias = keyAliasProp
            keyPassword = keyPassword
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui:1.7.2")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // Google Sign-in & Drive REST
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.google.api-client:google-api-client-android:2.5.0")
    implementation("com.google.http-client:google-http-client-android:1.44.1")
    implementation("com.google.apis:google-api-services-drive:v3-rev20230815-2.0.0")
}
