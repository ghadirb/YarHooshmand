# راهنمای امضای نسخه Release (برای حذف پیام‌های امنیتی نصب)

1) یک keystore بسازید (یک‌بار برای همیشه):
```bash
keytool -genkeypair -v -keystore my-release-key.jks -storetype JKS -keyalg RSA -keysize 2048 -validity 10000 -alias release
```
- فایل `my-release-key.jks` را در مسیر امن نگه‌دارید (داخل مخزن گیت نگذارید).

2) مقادیر را در `~/.gradle/gradle.properties` یا در فایل `gradle.properties` پروژه قرار دهید:
```
RELEASE_STORE_FILE=/absolute/path/to/my-release-key.jks
RELEASE_STORE_PASSWORD=your-store-pass
RELEASE_KEY_ALIAS=release
RELEASE_KEY_PASSWORD=your-key-pass
```

3) در `app/build.gradle` پیکربندی زیر را (اگر وجود نداشت) اضافه/فعال کنید:
```gradle
android {
    ...
    signingConfigs {
        release {
            storeFile file(RELEASE_STORE_FILE)
            storePassword RELEASE_STORE_PASSWORD
            keyAlias RELEASE_KEY_ALIAS
            keyPassword RELEASE_KEY_PASSWORD
        }
    }
    buildTypes {
        release {
            minifyEnabled false
            shrinkResources false
            signingConfig signingConfigs.release
        }
    }
}
```

4) بیلد:
```bash
./gradlew clean assembleRelease
```
خروجی در `app/build/outputs/apk/release/` خواهد بود.
