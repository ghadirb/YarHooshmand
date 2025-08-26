[app]

# اطلاعات پایه اپلیکیشن
title = YarHooshmand
package.name = yarhooshmand
package.domain = org.yarhooshmand

# مسیر و فایل‌های پروژه
source.dir = .
source.include_exts = py,png,jpg,kv,atlas,ttf,json

# نسخه اپلیکیشن
version = 1.0

# کتابخانه‌های مورد نیاز
requirements = python3,kivy,kivymd,requests

# تنظیمات ظاهری
orientation = portrait
fullscreen = 1

# دسترسی‌های اندروید
android.permissions = INTERNET,VIBRATE,RECEIVE_BOOT_COMPLETED

# تنظیمات اندروید
android.api = 33
android.minapi = 21
android.ndk = 23b
android.sdk_path =
android.ndk_path =
android.archs = armeabi-v7a

# وابستگی‌های Gradle برای پشتیبانی از رابط کاربری مدرن
android.gradle_dependencies = com.android.support:appcompat-v7:28.0.0

# آیکون و صفحه‌ی شروع (در صورت وجود)
presplash.filename = %(source.dir)s/data/presplash.png
icon.filename = %(source.dir)s/data/icon.png

# تنظیمات اضافی
android.allow_backup = True
android.logcat_filters = *:S python:D
android.entrypoint = main.py
android.manifest.intent_filters =

[buildozer]

# تنظیمات لاگ و هشدارها
log_level = 2
warn_on_root = 1
