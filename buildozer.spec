[app]

# اطلاعات پایه
title = MyApp
package.name = myapp
package.domain = org.myapp

# مسیر و فایل‌های پروژه
source.dir = .
source.include_exts = py,png,jpg,kv,atlas

# نسخه اپلیکیشن
version = 0.1

# کتابخانه‌های مورد نیاز
requirements = python3,kivy,kivymd,requests

# تنظیمات ظاهری
orientation = portrait
fullscreen = 1

# دسترسی‌ها
android.permissions = INTERNET

# تنظیمات اندروید
android.api = 33
android.minapi = 21
android.sdk = 24
android.ndk = 23b
android.arch = armeabi-v7a

# مسیرهای SDK و NDK (خالی بذار تا خودش تنظیم کنه)
android.sdk_path =
android.ndk_path =

# وابستگی‌های Gradle
android.gradle_dependencies = com.android.support:appcompat-v7:28.0.0

# تنظیمات اضافی
android.allow_backup = True
android.logcat_filters = *:S python:D
android.entrypoint = main.py
android.manifest.intent_filters =

[buildozer]

# سطح لاگ و هشدارها
log_level = 2
warn_on_root = 1
