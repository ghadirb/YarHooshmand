android {
    ...
    signingConfigs {
        create("release") {
            storeFile = file("release-keystore.jks")
            storePassword = "12345678"    // رمزی که هنگام ساخت keystore زدید
            keyAlias = "my-key-alias"     // alias که هنگام ساخت وارد کردید
            keyPassword = "12345678"      // می‌توانید همان رمز keystore باشد
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release") // اتصال SigningConfig
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
