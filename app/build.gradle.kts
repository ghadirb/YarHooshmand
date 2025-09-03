android {
    ...
    signingConfigs {
        create("release") {
            storeFile = file("release-keystore.jks") // مسیر keystore داخل پروژه
            storePassword = "YOUR_KEYSTORE_PASSWORD"
            keyAlias = "YOUR_KEY_ALIAS"
            keyPassword = "YOUR_KEY_PASSWORD"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release") // اضافه شد
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }
}
