plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'kotlin-kapt'
}

android {
    namespace "org.yarhooshmand.smartv3"
    compileSdk 34

    defaultConfig {
        applicationId "org.yarhooshmand.smartv3"
        minSdk 21
        targetSdk 34
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            shrinkResources false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
        debug {
            minifyEnabled false
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose true
    }

    composeOptions {
        kotlinCompilerExtensionVersion "1.5.3"
    }

    packagingOptions {
        resources {
            excludes += ["/META-INF/{AL2.0,LGPL2.1}"]
        }
    }
}

dependencies {
    // AndroidX
    implementation "androidx.core:core-ktx:1.13.1"
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.8.4"
    implementation "androidx.activity:activity-compose:1.9.1"

    // Compose
    implementation platform("androidx.compose:compose-bom:2024.06.00")
    implementation "androidx.compose.ui:ui"
    implementation "androidx.compose.ui:ui-graphics"
    implementation "androidx.compose.ui:ui-tooling-preview"
    implementation "androidx.compose.material3:material3:1.2.1"
    debugImplementation "androidx.compose.ui:ui-tooling"
    debugImplementation "androidx.compose.ui:ui-test-manifest"

    // Navigation Compose
    implementation "androidx.navigation:navigation-compose:2.8.0"

    // Room
    implementation "androidx.room:room-runtime:2.6.1"
    kapt "androidx.room:room-compiler:2.6.1"
    implementation "androidx.room:room-ktx:2.6.1"

    // OkHttp + Gson
    implementation "com.squareup.okhttp3:okhttp:4.12.0"
    implementation "com.google.code.gson:gson:2.10.1"

    // Coroutines
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1"

    // Testing
    testImplementation "junit:junit:4.13.2"
    androidTestImplementation "androidx.test.ext:junit:1.2.1"
    androidTestImplementation "androidx.test.espresso:espresso-core:3.6.1"
    androidTestImplementation platform("androidx.compose:compose-bom:2024.06.00")
    androidTestImplementation "androidx.compose.ui:ui-test-junit4"
}
