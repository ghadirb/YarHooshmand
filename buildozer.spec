[app]

# اطلاعات اپلیکیشن
title = MyApp
package.name = myapp
package.domain = org.example
version = 1.0
orientation = portrait
icon.filename = %(source.dir)s/icon.png
presplash.filename = %(source.dir)s/presplash.png

# مسیر سورس پایتون
source.dir = .
source.include_exts = py,png,jpg,kv,atlas

# Android
android.api = 30
android.minapi = 21
android.sdk = 30
android.build_tools_version = 30.0.3
android.ndk = 23b
android.arch = armeabi-v7a
android.release = 1
android.sdk_path = ./Android/Sdk
