android {
    namespace = "org.yarhooshmand.smartv3"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.yarhooshmand.smartv3"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "3.0"
    }

    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.10" }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}
